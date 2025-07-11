package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

import model.User;
import model.Skill;
import service.*;
import exception.ValidationException;

import java.util.List;
import java.util.Optional;

public class CodeviaFXApp extends Application {
    private UserService userService;
    private AuthService authService;
    private SkillService skillService;
    private QuizService quizService;
    private AchievementService achievementService;
    
    private User currentUser;
    private Stage primaryStage;
    private Scene loginScene, mainMenuScene, skillsScene, quizScene, achievementsScene, activeQuizScene;
    
    // UI Components
    private TextField emailField, usernameField;
    private PasswordField passwordField;
    private Label statusLabel, userInfoLabel;
    private RadioButton studentRadio, instructorRadio;
    private ComboBox<String> skillComboBox, themeComboBox, languageComboBox;
    private TextArea achievementsArea;
    private ListView<String> skillListView;
    private CheckBox rememberMeCheckBox, notificationsCheckBox;
    private Slider volumeSlider;
    private ProgressBar progressBar;
    
    // Quiz components
    private VBox quizContainer;
    private Label questionLabel, questionCountLabel, timerLabel;
    private RadioButton[] answerOptions;
    private ToggleGroup answersGroup;
    private Button submitAnswerButton, finishQuizButton;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<model.Question> currentQuizQuestions;
    private String currentQuizTopic;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeServices();
        createScenes();
        
        primaryStage.setTitle("Codevia - Modern E-Learning Platform");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeServices() {
        try {
            userService = new UserService();
            authService = new AuthService(userService);
            skillService = new SkillService();
            quizService = new QuizService();
            System.out.println("Services initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();
            // Create minimal services for offline operation
            createFallbackServices();
        }
    }
    
    private void createFallbackServices() {
        System.out.println("Creating fallback services for offline operation...");
        try {
            // Create basic services that work without Firebase
            userService = new UserService();
            authService = new AuthService(userService);
            skillService = new SkillService();
            quizService = new QuizService();
            System.out.println("✅ Fallback services created successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to create fallback services: " + e.getMessage());
            throw new RuntimeException("Failed to initialize services", e);
        }
    }

    private void createScenes() {
        loginScene = createLoginScene();
        mainMenuScene = createMainMenuScene();
        skillsScene = createSkillsScene();
        quizScene = createQuizScene();
        achievementsScene = createAchievementsScene();
        
        // Apply custom CSS to all scenes
        applyCSSToScene(loginScene);
        applyCSSToScene(mainMenuScene);
        applyCSSToScene(skillsScene);
        applyCSSToScene(quizScene);
        applyCSSToScene(achievementsScene);
    }
    
    private void applyCSSToScene(Scene scene) {
        try {
            // Load CSS from resources - try fixed version first
            String cssPath = getClass().getResource("/resources/style-fixed.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            System.out.println("Successfully loaded CSS stylesheet");
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS stylesheet: " + e.getMessage());
            // Try to load original if fixed version fails
            try {
                String fallbackCssPath = getClass().getResource("/resources/style.css").toExternalForm();
                scene.getStylesheets().add(fallbackCssPath);
                System.out.println("Loaded fallback CSS stylesheet");
            } catch (Exception e2) {
                System.err.println("Warning: Could not load fallback CSS stylesheet either: " + e2.getMessage());
            }
        }
    }

    private Scene createLoginScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: white;");

        // Title
        Label titleLabel = new Label("Welcome to Codevia!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#333"));
        titleLabel.setEffect(new DropShadow());

        // Login form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.getStyleClass().add("form-container");
        formContainer.setEffect(new DropShadow());

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("form-label");
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.getStyleClass().add("rounded-text-field");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("form-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("rounded-text-field");

        // User type selection
        Label userTypeLabel = new Label("User Type:");
        userTypeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        ToggleGroup userTypeGroup = new ToggleGroup();
        studentRadio = new RadioButton("Student");
        instructorRadio = new RadioButton("Instructor");
        studentRadio.setToggleGroup(userTypeGroup);
        instructorRadio.setToggleGroup(userTypeGroup);
        studentRadio.setSelected(true);
        
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(studentRadio, instructorRadio);

        // Remember me checkbox
        rememberMeCheckBox = new CheckBox("Remember me");
        rememberMeCheckBox.setStyle("-fx-font-size: 12px;");

        // Buttons
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().addAll("button", "primary-button", "rounded-button");
        loginButton.setOnAction(e -> performLogin());

        Button registerButton = new Button("Create Account");
        registerButton.getStyleClass().addAll("button", "secondary-button", "rounded-button");
        registerButton.setOnAction(e -> showRegisterDialog());

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Status label
        statusLabel = new Label("");
        statusLabel.setTextFill(Color.RED);
        statusLabel.setStyle("-fx-font-size: 12px;");

        formContainer.getChildren().addAll(
            emailLabel, emailField,
            passwordLabel, passwordField,
            userTypeLabel, radioBox,
            rememberMeCheckBox,
            buttonBox,
            statusLabel
        );

        root.getChildren().addAll(titleLabel, formContainer);
        return new Scene(root, 500, 650);
    }

    private Scene createMainMenuScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: rgba(248, 249, 250, 0.9); -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");
        
        userInfoLabel = new Label();
        userInfoLabel.setTextFill(Color.web("#333"));
        userInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button", "danger-button", "rounded-button");
        logoutButton.setOnAction(e -> logout());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(userInfoLabel, spacer, logoutButton);

        // Center content
        GridPane centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(30);
        centerGrid.setVgap(30);
        centerGrid.setPadding(new Insets(60));

        // Menu buttons
        Button skillsButton = createMenuButton("View Skills", "🎯");
        skillsButton.setOnAction(e -> {
            updateSkillsList();
            primaryStage.setScene(skillsScene);
        });

        Button quizButton = createMenuButton("Take Quiz", "📝");
        quizButton.setOnAction(e -> primaryStage.setScene(quizScene));

        Button achievementsButton = createMenuButton("Achievements", "🏆");
        achievementsButton.setOnAction(e -> {
            updateAchievements();
            primaryStage.setScene(achievementsScene);
        });

        Button settingsButton = createMenuButton("Settings", "⚙️");
        settingsButton.setOnAction(e -> showSettingsDialog());

        centerGrid.add(skillsButton, 0, 0);
        centerGrid.add(quizButton, 1, 0);
        centerGrid.add(achievementsButton, 0, 1);
        centerGrid.add(settingsButton, 1, 1);

        root.setTop(topBar);
        root.setCenter(centerGrid);

        return new Scene(root, 800, 600);
    }

    private Scene createSkillsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Title
        Label titleLabel = new Label("Available Skills");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));

        // Skills list
        skillListView = new ListView<>();
        skillListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10;");
        skillListView.setPrefHeight(300);
        
        // Populate skills
        updateSkillsList();

        // Skill selection combo box
        Label skillLabel = new Label("Select Skill to Unlock:");
        skillLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        skillComboBox = new ComboBox<>();
        skillComboBox.getItems().addAll("Java Basics", "OOP Concepts", "Data Structures", "Algorithms", "Web Development");
        skillComboBox.setPromptText("Choose a skill...");
        skillComboBox.setStyle("-fx-font-size: 14px;");

        // Progress bar
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #4CAF50;");

        // Buttons
        Button unlockButton = new Button("Unlock Skill");
        unlockButton.getStyleClass().addAll("button", "primary-button", "rounded-button");
        unlockButton.setOnAction(e -> unlockSelectedSkill());

        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().addAll("button", "secondary-button", "rounded-button");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(30));
        centerBox.getChildren().addAll(
            skillListView,
            skillLabel,
            skillComboBox,
            new Label("Progress:"),
            progressBar,
            new HBox(15, unlockButton, backButton)
        );

        root.setTop(titleLabel);
        root.setCenter(centerBox);

        return new Scene(root, 800, 600);
    }

    private Scene createQuizScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Title
        Label titleLabel = new Label("Take a Quiz");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));

        // Center content
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        // Quiz selection
        Label quizLabel = new Label("Select Quiz Topic:");
        quizLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.getItems().addAll("Java Basics", "OOP");
        quizComboBox.setPromptText("Choose a quiz topic...");
        quizComboBox.getStyleClass().add("combo-box");
        quizComboBox.setPrefWidth(200);

        // Difficulty selection
        Label difficultyLabel = new Label("Select Difficulty:");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton easyRadio = new RadioButton("Easy");
        RadioButton mediumRadio = new RadioButton("Medium");
        RadioButton hardRadio = new RadioButton("Hard");
        
        easyRadio.setToggleGroup(difficultyGroup);
        mediumRadio.setToggleGroup(difficultyGroup);
        hardRadio.setToggleGroup(difficultyGroup);
        easyRadio.setSelected(true);

        HBox difficultyBox = new HBox(15);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.getChildren().addAll(easyRadio, mediumRadio, hardRadio);

        // Time limit
        Label timeLabel = new Label("Time Limit:");
        timeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        Slider timeSlider = new Slider(5, 60, 15);
        timeSlider.setShowTickLabels(true);
        timeSlider.setShowTickMarks(true);
        timeSlider.setMajorTickUnit(15);
        timeSlider.setMinorTickCount(4);
        timeSlider.setBlockIncrement(5);
        
        Label timeValueLabel = new Label("15 minutes");
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            timeValueLabel.setText(String.format("%.0f minutes", newVal.doubleValue())));

        // Buttons
        Button startQuizButton = new Button("Start Quiz");
        startQuizButton.getStyleClass().addAll("button", "primary-button", "rounded-button");
        startQuizButton.setOnAction(e -> startQuiz(quizComboBox.getValue()));

        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().addAll("button", "secondary-button", "rounded-button");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        centerBox.getChildren().addAll(
            quizLabel, quizComboBox,
            difficultyLabel, difficultyBox,
            timeLabel, timeSlider, timeValueLabel,
            startQuizButton, backButton
        );

        root.setTop(titleLabel);
        root.setCenter(centerBox);

        return new Scene(root, 800, 600);
    }

    private Scene createAchievementsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Title
        Label titleLabel = new Label("Your Achievements");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));

        // Achievements area
        achievementsArea = new TextArea();
        achievementsArea.setEditable(false);
        achievementsArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10; -fx-font-size: 14px;");
        achievementsArea.setPrefHeight(350);

        // Back button
        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().addAll("button", "secondary-button", "rounded-button");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));
        centerBox.getChildren().addAll(achievementsArea, backButton);

        root.setTop(titleLabel);
        root.setCenter(centerBox);

        return new Scene(root, 800, 600);
    }

    private Button createMenuButton(String text, String emoji) {
        Button button = new Button(emoji + "\n" + text);
        button.setPrefSize(180, 140);
        button.getStyleClass().addAll("menu-button", "rounded-button");
        button.setEffect(new DropShadow());
        
        return button;
    }

    private void performLogin() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                return;
            }

            // Mock authentication for demo
            currentUser = userService.findUserByEmail(email);
            if (currentUser == null) {
                statusLabel.setText("User not found");
                return;
            }

            if (!currentUser.getPassword().equals(password)) {
                statusLabel.setText("Invalid password");
                return;
            }

            // Initialize achievement service for logged in user
            achievementService = new AchievementService(currentUser);

            statusLabel.setText("Login successful!");
            updateUserInfo();
            primaryStage.setScene(mainMenuScene);

            // Clear fields
            emailField.clear();
            passwordField.clear();
            statusLabel.setText("");

        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
        }
    }

    private void showRegisterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Register for Codevia");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        content.getChildren().addAll(
            new Label("Username:"), usernameField,
            new Label("Email:"), emailField,
            new Label("Password:"), passwordField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String username = usernameField.getText().trim();
                    String email = emailField.getText().trim();
                    String password = passwordField.getText();

                    currentUser = userService.createUser(username, email, password);
                    showAlert("Success", "Registration successful! Welcome " + username + "!");
                } catch (ValidationException ex) {
                    showAlert("Error", "Registration failed: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showSettingsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Application Settings");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Theme selection
        Label themeLabel = new Label("Theme:");
        themeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Light", "Dark", "Blue", "Green");
        themeComboBox.setValue("Light");

        // Language selection
        Label languageLabel = new Label("Language:");
        languageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Indonesian", "Spanish", "French");
        languageComboBox.setValue("English");

        // Notifications
        notificationsCheckBox = new CheckBox("Enable Notifications");
        notificationsCheckBox.setSelected(true);

        // Volume control
        Label volumeLabel = new Label("Volume:");
        volumeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);

        content.getChildren().addAll(
            themeLabel, themeComboBox,
            languageLabel, languageComboBox,
            notificationsCheckBox,
            volumeLabel, volumeSlider
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText("Welcome, " + currentUser.getUsername() +
                    " | Level: " + currentUser.getUserLevel() +
                    " | XP: " + currentUser.getExperiencePoint());
        }
    }

    private void updateSkillsList() {
        if (skillListView != null) {
            skillListView.getItems().clear();
            for (Skill skill : skillService.getSkills()) {
                String status = skill.isUnlocked() ? "✅" : "🔒";
                skillListView.getItems().add(status + " " + skill.getSkillName() + " - " + skill.getDescription());
            }
        }
    }

    private void updateAchievements() {
        if (achievementsArea != null && currentUser != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("🏆 Your Achievements:\n\n");

            if (currentUser.getAchievements().isEmpty()) {
                sb.append("No achievements yet. Keep learning to unlock achievements!");
            } else {
                for (String achievement : currentUser.getAchievements()) {
                    sb.append("✅ ").append(achievement).append("\n");
                }
            }

            achievementsArea.setText(sb.toString());
        }
    }

    private void unlockSelectedSkill() {
        String selectedSkill = skillComboBox.getValue();
        if (selectedSkill != null) {
            showAlert("Success", "Skill unlocked: " + selectedSkill);
            progressBar.setProgress(progressBar.getProgress() + 0.2);
            updateSkillsList();
        } else {
            showAlert("Error", "Please select a skill to unlock");
        }
    }

    private void startQuiz(String quizTopic) {
        if (quizTopic != null) {
            currentQuizTopic = quizTopic;
            model.Quiz quiz = quizService.getQuizBySkillName(quizTopic);
            if (quiz != null) {
                currentQuizQuestions = quiz.getQuestions();
                currentQuestionIndex = 0;
                score = 0;
                createActiveQuizScene();
                primaryStage.setScene(activeQuizScene);
            } else {
                showAlert("Error", "Quiz not available for: " + quizTopic);
            }
        } else {
            showAlert("Error", "Please select a quiz topic");
        }
    }
    
    private void createActiveQuizScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("quiz-scene");
        
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("quiz-header");
        
        Label titleLabel = new Label("Quiz: " + currentQuizTopic);
        titleLabel.getStyleClass().addAll("title-label", "quiz-title");
        
        questionCountLabel = new Label();
        questionCountLabel.getStyleClass().add("question-count");
        
        header.getChildren().addAll(titleLabel, questionCountLabel);
        
        // Question container
        quizContainer = new VBox(20);
        quizContainer.setAlignment(Pos.CENTER);
        quizContainer.setPadding(new Insets(40));
        quizContainer.getStyleClass().add("quiz-container");
        
        // Question label
        questionLabel = new Label();
        questionLabel.getStyleClass().addAll("question-label", "quiz-question");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(600);
        
        // Answer options
        answersGroup = new ToggleGroup();
        answerOptions = new RadioButton[4];
        VBox answersBox = new VBox(15);
        answersBox.setAlignment(Pos.CENTER_LEFT);
        answersBox.setPadding(new Insets(20));
        answersBox.getStyleClass().add("answers-container");
        
        for (int i = 0; i < 4; i++) {
            answerOptions[i] = new RadioButton();
            answerOptions[i].setToggleGroup(answersGroup);
            answerOptions[i].getStyleClass().add("quiz-option");
            answerOptions[i].setMaxWidth(550);
            answerOptions[i].setWrapText(true);
            answersBox.getChildren().add(answerOptions[i]);
        }
        
        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        
        submitAnswerButton = new Button("Next Question");
        submitAnswerButton.getStyleClass().addAll("button", "primary-button", "rounded-button");
        submitAnswerButton.setOnAction(e -> submitAnswer());
        
        finishQuizButton = new Button("Finish Quiz");
        finishQuizButton.getStyleClass().addAll("button", "secondary-button", "rounded-button");
        finishQuizButton.setOnAction(e -> finishQuiz());
        finishQuizButton.setVisible(false);
        
        Button backToMenuButton = new Button("Back to Menu");
        backToMenuButton.getStyleClass().addAll("button", "danger-button", "rounded-button");
        backToMenuButton.setOnAction(e -> {
            if (showConfirmDialog("Are you sure you want to exit the quiz?", "Your progress will be lost.")) {
                primaryStage.setScene(mainMenuScene);
            }
        });
        
        buttonBox.getChildren().addAll(submitAnswerButton, finishQuizButton, backToMenuButton);
        
        quizContainer.getChildren().addAll(questionLabel, answersBox, buttonBox);
        
        root.setTop(header);
        root.setCenter(quizContainer);
        
        activeQuizScene = new Scene(root, 800, 600);
        applyCSSToScene(activeQuizScene);
        
        displayCurrentQuestion();
    }
    
    private void displayCurrentQuestion() {
        if (currentQuestionIndex < currentQuizQuestions.size()) {
            model.Question question = currentQuizQuestions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestionText());
            questionCountLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + currentQuizQuestions.size());
            
            List<String> options = question.getOptions();
            for (int i = 0; i < answerOptions.length; i++) {
                if (i < options.size()) {
                    answerOptions[i].setText(options.get(i));
                    answerOptions[i].setVisible(true);
                } else {
                    answerOptions[i].setVisible(false);
                }
            }
            
            // Clear previous selection
            answersGroup.selectToggle(null);
            
            // Show/hide buttons based on question index
            if (currentQuestionIndex == currentQuizQuestions.size() - 1) {
                submitAnswerButton.setText("Submit Answer");
                finishQuizButton.setVisible(true);
            } else {
                submitAnswerButton.setText("Next Question");
                finishQuizButton.setVisible(false);
            }
        }
    }
    
    private void submitAnswer() {
        RadioButton selectedAnswer = (RadioButton) answersGroup.getSelectedToggle();
        if (selectedAnswer == null) {
            showAlert("No Answer Selected", "Please select an answer before continuing.");
            return;
        }
        
        // Check if answer is correct
        model.Question question = currentQuizQuestions.get(currentQuestionIndex);
        int selectedIndex = -1;
        for (int i = 0; i < answerOptions.length; i++) {
            if (answerOptions[i] == selectedAnswer) {
                selectedIndex = i;
                break;
            }
        }
        
        if (selectedIndex == question.getCorrectAnswer()) {
            score++;
            showAlert("Correct!", "Well done! You got the right answer.");
        } else {
            showAlert("Incorrect", "The correct answer was: " + question.getOptions().get(question.getCorrectAnswer()));
        }
        
        currentQuestionIndex++;
        
        if (currentQuestionIndex < currentQuizQuestions.size()) {
            displayCurrentQuestion();
        } else {
            finishQuiz();
        }
    }
    
    private void finishQuiz() {
        // Calculate results
        int totalQuestions = currentQuizQuestions.size();
        double percentage = (double) score / totalQuestions * 100;
        int earnedXP = score * 20;
        
        // Award XP to user
        if (currentUser != null) {
            currentUser.gainExperiencePoint(earnedXP);
        }
        
        // Show results
        String resultMessage = String.format(
            "Quiz Complete!\n\n" +
            "Topic: %s\n" +
            "Score: %d/%d (%.1f%%)\n" +
            "XP Earned: %d\n\n" +
            "Great job! Keep learning!",
            currentQuizTopic, score, totalQuestions, percentage, earnedXP
        );
        
        showAlert("Quiz Results", resultMessage);
        
        // Update user info and return to main menu
        updateUserInfo();
        primaryStage.setScene(mainMenuScene);
    }
    
    private boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void logout() {
        currentUser = null;
        primaryStage.setScene(loginScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
