#version 440 core

// ============================
// Material and Light Structures
// ============================
struct Material {
    vec3 ambientColor;
    float ambientStrength;
    vec3 diffuseColor;
    vec3 specularColor;
    float shininess;
};

struct LightSource {
    vec3 position;	
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    bool bActive;
};

// ============================
// Inputs from Vertex Shader
// ============================
in vec3 fragmentPosition;
in vec3 fragmentVertexNormal;
in vec2 fragmentTextureCoordinate;

// ============================
// Outputs
// ============================
out vec4 outFragmentColor;

// ============================
// Uniforms (from C++)
// ============================
uniform bool bUseTexture = false;
uniform bool bUseLighting = false;
uniform vec4 objectColor = vec4(1.0f);
uniform sampler2D objectTexture;
uniform vec3 viewPosition;
uniform vec2 UVscale = vec2(1.0f, 1.0f);

// Lights — match your SceneManager.cpp definitions
uniform LightSource directionalLight;
uniform LightSource pointLights[2];
uniform LightSource spotLight;

// Material
uniform Material material;

// ============================
// Function Prototypes
// ============================
vec3 CalcDirectionalLight(LightSource light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 CalcSpotLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir);

void main()
{
    // Lighting control
    if (bUseLighting)
    {
        vec3 norm = normalize(fragmentVertexNormal);
        vec3 viewDir = normalize(viewPosition - fragmentPosition);
        vec3 result = vec3(0.0f);

        // =============== Directional Light ===============
        if (directionalLight.bActive)
            result += CalcDirectionalLight(directionalLight, norm, viewDir);

        // =============== Point Lights =====================
        for (int i = 0; i < 2; i++)
        {
            if (pointLights[i].bActive)
                result += CalcPointLight(pointLights[i], norm, fragmentPosition, viewDir);
        }

        // =============== Spot Light =======================
        if (spotLight.bActive)
            result += CalcSpotLight(spotLight, norm, fragmentPosition, viewDir);

        // Apply texture or base color
        if (bUseTexture)
        {
            vec4 texColor = texture(objectTexture, fragmentTextureCoordinate * UVscale);
            outFragmentColor = vec4(result * texColor.rgb, texColor.a);
        }
        else
        {
            outFragmentColor = vec4(result * objectColor.rgb, objectColor.a);
        }
    }
    else
    {
        // Lighting disabled
        if (bUseTexture)
            outFragmentColor = texture(objectTexture, fragmentTextureCoordinate * UVscale);
        else
            outFragmentColor = objectColor;
    }
}

// ============================
// Lighting Calculations
// ============================

// Directional light: acts like sunlight, same direction everywhere
vec3 CalcDirectionalLight(LightSource light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.position);  // using 'position' as direction vector here
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    vec3 ambient = light.ambient * material.ambientColor * material.ambientStrength;
    vec3 diffuse = light.diffuse * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;
    return (ambient + diffuse + specular);
}

// Point light: diminishes with distance
vec3 CalcPointLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    // Attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (1.0 + 0.15 * distance + 0.5 * (distance * distance));

    vec3 ambient = light.ambient * material.ambientColor * material.ambientStrength;
    vec3 diffuse = light.diffuse * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;

    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

// Spot light: cone of illumination with cutoff
vec3 CalcSpotLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    // Spotlight intensity using cutoff angles
    float theta = dot(lightDir, normalize(-light.position));  // using position as direction vector
    float epsilon = 0.05; // smooth edge
    float intensity = clamp((theta - 0.75) / epsilon, 0.0, 1.0);

    vec3 ambient = light.ambient * material.ambientColor * material.ambientStrength;
    vec3 diffuse = light.diffuse * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;

    return intensity * (ambient + diffuse + specular);
}
