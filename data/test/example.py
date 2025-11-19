# Sample Python file

class Greeter:
    def __init__(self, name):
        self.name = name

    def greet(self):
        if self.name:
            print(f"Hello, {self.name}")
        else:
            print("Hello")
