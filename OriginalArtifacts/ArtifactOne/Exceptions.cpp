// Exceptions.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <stdexcept>

// Added in a custom exception to be used under do_custom_application_logic
class CustomException : public std::exception
{
public:
    const char* what() const noexcept override
    {
        return "A custom exception has occurred!";
    }
};

bool do_even_more_custom_application_logic()
{
    // Throw a custom exception and catch it in main
    throw std::logic_error("Standard exception occured!");

    return true;
}
void do_custom_application_logic()
{
    // Wrap the call to do_even_more_custom_application_logic and throw a
    // standard exception
    try
    {
        do_even_more_custom_application_logic();
    }
    catch (const std::exception& e)
    {
        std::cerr << "Exception caught (standard): " << e.what() << std::endl;
    }

    // Throw a custom exception and catch it in main
    try
    {
        throw CustomException();
    }
    catch (const CustomException& e)
    {
        std::cerr << "Exception caught (custom): " << e.what() << std::endl;
    }

    std::cout << "Exiting the custom application logic." << std::endl;

}

float divide(float num, float den)
{
    // Throw an exception to deal with divide by zero errors using
    // a standard C++ defined exception and catch it in main
    if (den == 0)
    {
        throw std::runtime_error("Divide by zero error occurred!");
    }

    return (num / den);
}

void do_division() noexcept
{
    // Created an exception handler to capture ONLY the exception thrown
    // by divide.
    try
    {
        float numerator = 10.0f;
        float denominator = 0;

        auto result = divide(numerator, denominator);
        std::cout << "divide(" << numerator << ", " << denominator << ") = " << result << std::endl;
    }
    catch (const std::exception& e)
    {
        std::cerr << "Exception caught (divide by zero): " << e.what() << std::endl;
    }
}

int main()
{
    std::cout << "The exception test have initiated!" << std::endl;
    
    try
    {
        do_division();
        do_custom_application_logic();
    }
    
    // Exception handler that catches the custom exception
    catch (const CustomException& e)
    {
        std::cerr << "Custom Exception caught in main: " << e.what() << std::endl;
    }

    // Exception handler that catches the standard exception
    catch (const std::exception& e)
    {
        std::cerr << "Exception caught in main: " << e.what() << std::endl;
    }

    // Exception handler that catches an uncaught exception
    catch (...)
    {
        std::cerr << "Unhandled exception caught in main." << std::endl;
    }

    // Message to notify that the test is completed
    std::cout << "The exceptions test have completed!" << std::endl;

    return 0;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu