# Codevia - Interactive Learning Platform

A Java-based interactive learning platform with Firebase integration for managing user data, skills, quizzes, and achievements.

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

1. **Java 11 or higher**
   ```bash
   java -version
   ```

2. **Maven 3.6 or higher**
   ```bash
   # Install using Homebrew (macOS)
   brew install maven
   
   # Or download from: https://maven.apache.org/download.cgi
   ```

3. **Firebase Service Account Key**
   - Make sure `Codevia Firebase Admin SDK.json` is in the project root directory
   - This file contains your Firebase credentials

## 🚀 Quick Start

### Option 1: Using the Run Script (Recommended)
```bash
./run_project.sh
```

### Option 2: Manual Setup
```bash
# 1. Install dependencies
mvn clean install

# 2. Compile the project
mvn compile

# 3. Run the application
mvn exec:java -Dexec.mainClass="app.Main"
```

## 📁 Project Structure

```
OOPMidTestRangga/
├── src/
│   ├── app/
│   │   └── Main.java                 # Main application entry point
│   ├── model/                        # Data models
│   │   ├── User.java
│   │   ├── Skill.java
│   │   ├── Quiz.java
│   │   └── ...
│   ├── service/                      # Business logic services
│   │   ├── FirebaseService.java      # Firebase database operations
│   │   ├── UserService.java
│   │   ├── AuthService.java
│   │   └── ...
│   ├── interface_/                   # Interface definitions
│   ├── exception/                    # Custom exceptions
│   ├── gui/                         # GUI components
│   └── pom.xml                      # Maven configuration
├── Codevia Firebase Admin SDK.json  # Firebase credentials
├── run_project.sh                   # Automated run script
└── README.md                        # This file
```

## 🔧 Configuration

### Firebase Setup
1. Make sure your Firebase service account key is named `Codevia Firebase Admin SDK.json`
2. Place it in the project root directory
3. The application will automatically initialize Firebase on startup

### Database Configuration
The application uses Firebase Firestore for data persistence. Collections include:
- `users` - User accounts and profiles
- `skills` - Available programming skills
- `quizzes` - Quiz questions and answers
- `achievements` - User achievements and progress

## 🎮 Features

### Authentication
- User registration and login
- Secure password handling
- Session management

### Learning System
- **Skills Management**: Browse and unlock programming skills
- **Interactive Quizzes**: Test your knowledge with skill-based quizzes
- **Daily Challenges**: Complete daily programming challenges
- **Achievement System**: Earn badges and track progress
- **Level System**: Gain XP and level up

### User Interface
- Console-based menu system
- Clear navigation and feedback
- Real-time progress tracking

## 🔍 Application Flow

1. **Welcome Screen**
   - Register new account or login
   - Authentication validation

2. **Main Menu**
   - View available skills
   - Unlock new skills
   - Take quizzes
   - Check daily challenges
   - View achievements
   - Exit application

3. **Interactive Learning**
   - Complete quizzes to earn XP
   - Unlock skills by meeting requirements
   - Track progress through achievement system

## 🛠️ Firebase Integration

The project includes a complete Firebase implementation:

### FirebaseService Features
- **Real-time Database Operations**: Save, get, update, delete
- **Collection Management**: Handle multiple data collections
- **Error Handling**: Robust exception handling
- **Singleton Pattern**: Efficient resource management

### Database Operations
```java
// Save user data
firebaseService.save("users", userId, userData);

// Retrieve user data
Map<String, Object> userData = firebaseService.get("users", userId);

// Update user progress
firebaseService.update("users", userId, progressData);

// Delete user data
firebaseService.delete("users", userId);
```

## 🐛 Troubleshooting

### Common Issues

1. **Firebase Connection Error**
   ```
   Failed to connect to Firebase: [error message]
   ```
   - Check if `Codevia Firebase Admin SDK.json` exists
   - Verify Firebase credentials are valid
   - Ensure internet connection is active

2. **Maven Build Error**
   ```
   mvn: command not found
   ```
   - Install Maven using Homebrew: `brew install maven`
   - Or download from Maven's official website

3. **Java Version Error**
   ```
   java.lang.UnsupportedClassVersionError
   ```
   - Ensure Java 11 or higher is installed
   - Check Java version: `java -version`

4. **Compilation Errors**
   ```
   package com.google.firebase does not exist
   ```
   - Run `mvn clean install` to download dependencies
   - Check internet connection for Maven downloads

## 📊 Dependencies

The project uses the following main dependencies:

- **Firebase Admin SDK**: Database operations
- **SwingX**: Enhanced GUI components
- **Jackson**: JSON processing
- **SLF4J**: Logging framework

## 🎯 Usage Tips

1. **First Run**: Create a new account to get started
2. **Skill Progression**: Complete easier skills before attempting advanced ones
3. **Daily Challenges**: Check daily challenges for bonus XP
4. **Achievement Tracking**: Monitor your progress in the achievements section
5. **Data Persistence**: All progress is automatically saved to Firebase

## 📝 Development Notes

### Code Architecture
- **MVC Pattern**: Clear separation of concerns
- **Singleton Pattern**: Used for database service
- **Exception Handling**: Comprehensive error management
- **Interface-Based Design**: Flexible and extensible architecture

### Future Enhancements
- Web-based GUI interface
- Real-time multiplayer challenges
- Advanced analytics and reporting
- Mobile app integration

## 🤝 Contributing

This is a student project for OOP Programming Mid-Term Test. The codebase demonstrates:
- Object-oriented programming principles
- Database integration with Firebase
- User authentication and session management
- Interactive console-based user interface

## 📞 Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are installed
3. Ensure Firebase credentials are properly configured
4. Run the automated setup script for easiest setup

---

**Happy Learning with Codevia! 🚀**
