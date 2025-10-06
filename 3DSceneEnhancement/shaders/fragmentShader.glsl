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
uniform bool  bUseTexture      = false;
uniform bool  bUseLighting     = false;
uniform vec4  objectColor      = vec4(1.0f);
uniform sampler2D objectTexture;
uniform vec3  viewPosition;
uniform vec2  UVscale          = vec2(1.0f, 1.0f);

// === Reflection controls ===
uniform bool  u_ReflectionEnabled = false;
uniform samplerCube u_EnvMap;
uniform float u_Reflectivity = 0.0;

// Lights
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

// ============================
// Main Shader
// ============================
void main()
{
    vec3 norm = normalize(fragmentVertexNormal);
    vec4 baseColor;

    // ======== Compute Base Lighting/Color ========
    if (bUseLighting)
    {
        vec3 viewDir = normalize(viewPosition - fragmentPosition);
        vec3 result = vec3(0.0);

        if (directionalLight.bActive)
            result += CalcDirectionalLight(directionalLight, norm, viewDir);

        for (int i = 0; i < 2; i++)
            if (pointLights[i].bActive)
                result += CalcPointLight(pointLights[i], norm, fragmentPosition, viewDir);

        if (spotLight.bActive)
            result += CalcSpotLight(spotLight, norm, fragmentPosition, viewDir);

        if (bUseTexture)
        {
            vec4 texColor = texture(objectTexture, fragmentTextureCoordinate * UVscale);
            baseColor = vec4(result * texColor.rgb, texColor.a);
        }
        else
        {
            baseColor = vec4(result * objectColor.rgb, objectColor.a);
        }
    }
    else
    {
        if (bUseTexture)
            baseColor = texture(objectTexture, fragmentTextureCoordinate * UVscale);
        else
            baseColor = objectColor;
    }

    // ======== Optional Reflection Blending ========
    if (u_ReflectionEnabled)
    {
        vec3 I = normalize(fragmentPosition - viewPosition);
        vec3 R = reflect(I, normalize(fragmentVertexNormal));
        vec4 reflectionColor = texture(u_EnvMap, R);

        // Blend lighting result with reflection
        baseColor = mix(baseColor, reflectionColor, u_Reflectivity);

        // Add partial transparency for glass effect
        baseColor.a = 0.6;
    }

    outFragmentColor = baseColor;
}

// ============================
// Lighting Calculations
// ============================

// Directional light
vec3 CalcDirectionalLight(LightSource light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.position);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    vec3 ambient  = light.ambient  * material.ambientColor * material.ambientStrength;
    vec3 diffuse  = light.diffuse  * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;
    return ambient + diffuse + specular;
}

// Point light
vec3 CalcPointLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (1.0 + 0.15 * distance + 0.5 * distance * distance);

    vec3 ambient  = light.ambient  * material.ambientColor * material.ambientStrength;
    vec3 diffuse  = light.diffuse  * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;

    return attenuation * (ambient + diffuse + specular);
}

// Spot light
vec3 CalcSpotLight(LightSource light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    float theta = dot(lightDir, normalize(-light.position));
    float epsilon = 0.05;
    float intensity = clamp((theta - 0.75) / epsilon, 0.0, 1.0);

    vec3 ambient  = light.ambient  * material.ambientColor * material.ambientStrength;
    vec3 diffuse  = light.diffuse  * diff * material.diffuseColor;
    vec3 specular = light.specular * spec * material.specularColor;

    return intensity * (ambient + diffuse + specular);
}
