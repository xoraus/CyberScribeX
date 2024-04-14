# 🚀 CyberScribeX 🖋️

Welcome to CyberScribeX! The ultimate Blogging Application API project, where creativity meets technology. 🌐✨

## 📖 Introduction

This project is aimed at developing a robust blogging application with a focus on efficient Posts and Comments management, user Authentication and Authorization, and Category organization. The application is built with RESTful APIs to ensure seamless integration and scalability.

## High-Level Requirements

1. **Posts Management**: Create, Read, Update, and Delete Posts with Pagination and Sorting Support.
2. **Comments Management**: Create, Read, Update, and Delete Comments for Blog Posts.
3. **Authentication and Authorization**: Implement Registration, Login, and Security measures.
4. **Category Management**: Create, Read, Update, and Delete Categories.


## Exceptions Handling and Validations

1. Handle exceptions and errors, returning proper error responses to the client.
2. Validate REST API requests and send validation error responses to the client.

## Securing REST APIs

1. Secure REST APIs using Database Authentication.
2. Implement LogIn/SignIn REST API.
3. Implement Register/SignUp REST API.
4. Use JWT (JSON Web Token) token-based Authentication to secure the REST APIs.
5. Implement Role-based security with ADMIN and USER roles.


## 💻 Usage

To harness the power of these APIs, make HTTP requests to the specified endpoints using the appropriate methods (GET, POST, PUT, DELETE). Don't forget your authentication tokens!

Certainly! For a Spring Boot project in IntelliJ, the steps would be different. Here's the modified installation section:

## 🛠️ Installation

1. **Clone the repository:** 
   - Open IntelliJ IDEA and choose "Checkout from Version Control" > "Git".
   - Enter your Git repository URL and click "Clone."

2. **Import the project in IntelliJ IDEA:**
   - Open IntelliJ IDEA.
   - Choose "File" > "Open" and select the cloned project directory.

3. **Build the project:**
   - IntelliJ IDEA will automatically detect the project's build file (e.g., `pom.xml` for Maven).
   - Click "Build" > "Build Project" to download dependencies and build the project.

4. **Configure application properties:**
   - If your application relies on external configurations or properties, configure them in the `application.properties` or `application.yml` file. For example, database connection details.

5. **Run the application:**
   - Find the main class that contains the `main` method (usually annotated with `@SpringBootApplication`).
   - Right-click on the class and choose "Run" to start the Spring Boot application.

6. **Verify the application:**
   - Open a web browser and go to `http://localhost:8080` (or the port specified in your application). You should see the application running.

7. **Explore APIs:**
   - Use tools like Postman or Swagger UI to explore and test the available APIs.

**Note:** Ensure you have a compatible JDK installed on your system, and IntelliJ IDEA is configured to use that JDK.

## 🤝 Contributing

We welcome contributions from the community. If you find a bug or have an enhancement in mind, please open an issue or submit a pull request.

## 📄 License

This project is licensed under the [MIT License](LICENSE). Feel free to unleash your creativity!
