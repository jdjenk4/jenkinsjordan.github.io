"""
CS-499-10453-M01 Computer Science Capstone 2025
Southern New Hampshire University
Jordan Jenkins

Artifact One

The purpose of this program is to showcase the translation of C++ language to Python
language. By taking a training program, exceptions.cpp, and translating that program into
Python. The new Python file is called ExceptionsEnhancement.py.
"""


# Define a custom exception
class CustomException(Exception):
    def __init__(self, message="A custom exception has occurred!"):
        super().__init__(message)


def do_even_more_custom_application_logic():
    # Always raise a standard exception (similar to logic_error in C++)
    raise ValueError("Standard exception occurred!")
    # Note: no return here, function always raises


def do_custom_application_logic():
    # Wrap call to do_even_more_custom_application_logic and catch it
    try:
        do_even_more_custom_application_logic()
    except Exception as e:
        print(f"Exception caught (standard): {e}")

    # Raise a custom exception and catch it here
    try:
        raise CustomException()
    except CustomException as e:
        print(f"Exception caught (custom): {e}")

    print("Exiting the custom application logic.")


def divide(num, den):
    # Raise exception for divide by zero
    if den == 0:
        raise ZeroDivisionError("Divide by zero error occurred!")
    return num / den


def do_division():
    # Handle divide function exceptions locally
    try:
        numerator = 10.0
        denominator = 0
        result = divide(numerator, denominator)
        print(f"divide({numerator}, {denominator}) = {result}")
    except Exception as e:
        print(f"Exception caught (divide by zero): {e}")


def main():
    print("The exception test has initiated!")

    try:
        do_division()
        do_custom_application_logic()
    except CustomException as e:
        print(f"Custom Exception caught in main: {e}")
    except Exception as e:
        print(f"Exception caught in main: {e}")
    except BaseException as e:
        print(f"Unhandled exception caught in main: {e}")

    print("The exceptions test has completed!")


if __name__ == "__main__":
    main()
