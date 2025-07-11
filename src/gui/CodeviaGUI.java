// gui/CodeviaGUI.java
package gui;

import model.*;
import service.*;
import exception.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CodeviaGUI extends JFrame {
    private UserService userService;
    private SkillService skillService;
    private QuizService quizService;
    private AchievementService achievementService;

    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // GUI Components
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel, userInfoLabel;
    private JPanel skillsContainer;
    private JPanel quizPanel;
    private JLabel currentQuestionLabel;
    private JRadioButton[] answerOptions;
    private ButtonGroup answerGroup;
    private JButton nextButton;
    private JLabel scoreLabel;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Quiz currentQuiz;

    // Simple white and blue color scheme
    private final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private final Color LIGHT_BLUE = new Color(230, 245, 255);
    private final Color ACCENT_YELLOW = new Color(255, 193, 7);
    private final Color TEXT_DARK = new Color(33, 37, 41);
    private final Color TEXT_GRAY = new Color(108, 117, 125);
    private final Color BACKGROUND_LIGHT = Color.WHITE;
    private final Color CARD_WHITE = Color.WHITE;
    private final Color BORDER_LIGHT = new Color(222, 226, 230);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private final Color ERROR_RED = new Color(220, 53, 69);
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 8);
    
    // Simplified color constants - all white and blue
    private final Color APPLE_SUPER_LIGHT_GRAY = Color.WHITE;
    private final Color APPLE_WHITE = Color.WHITE;
    private final Color APPLE_DARK_GRAY = new Color(33, 37, 41);
    private final Color APPLE_GRAY = new Color(108, 117, 125);
    private final Color APPLE_LIGHT_GRAY = new Color(248, 249, 250);
    private final Color APPLE_BLUE = new Color(0, 123, 255);
    private final Color APPLE_GREEN = new Color(40, 167, 69);
    private final Color APPLE_ORANGE = new Color(255, 193, 7);
    private final Color APPLE_RED = new Color(220, 53, 69);

    public CodeviaGUI() {
        initializeServices();
        initializeGUI();
    }

    private void initializeServices() {
        System.out.println("DEBUG: Initializing services...");
        userService = new UserService();
        skillService = new SkillService();
        quizService = new QuizService();
        
        // Initialize first skill as unlocked
        List<Skill> skills = skillService.getSkills();
        System.out.println("DEBUG: Found " + skills.size() + " skills");
        if (!skills.isEmpty()) {
            skills.get(0).setUnlocked(true);
            System.out.println("DEBUG: First skill '" + skills.get(0).getSkillName() + "' has been unlocked");
        }
        
        // Print all skills status
        for (Skill skill : skills) {
            System.out.println("DEBUG: Skill: " + skill.getSkillName() + ", Unlocked: " + skill.isUnlocked() + ", Required XP: " + skill.getRequiredXP());
        }
    }

    private void initializeGUI() {
        setTitle("Codevia - Learn. Code. Excel.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_LIGHT);
        
        // Set macOS-like appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_LIGHT);

        // Create different panels
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegistrationPanel(), "REGISTER");
        mainPanel.add(createMainMenuPanel(), "MAIN_MENU");
        mainPanel.add(createSkillsPanel(), "SKILLS");
        mainPanel.add(createQuizSelectionPanel(), "QUIZ_SELECTION");
        mainPanel.add(createQuizPanel(), "QUIZ");
        mainPanel.add(createAchievementsPanel(), "ACHIEVEMENTS");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_LIGHT);
        
        // Header panel (empty now, just for spacing)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30));
        headerPanel.setBackground(BACKGROUND_LIGHT);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        // Hero title
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(BACKGROUND_LIGHT);
        heroPanel.setBorder(new EmptyBorder(40, 0, 60, 0));
        
        JLabel heroTitle = new JLabel("Codevia Learning Platform");
        heroTitle.setFont(new Font("Inter", Font.BOLD, 48));
        heroTitle.setForeground(TEXT_DARK);
        heroTitle.setHorizontalAlignment(SwingConstants.CENTER);
        heroPanel.add(heroTitle, BorderLayout.CENTER);
        
        // Login card
        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setBackground(BACKGROUND_LIGHT);
        cardContainer.setBorder(new EmptyBorder(0, 40, 80, 40));
        
        JPanel loginCard = createModernCard();
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(420, 480));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        
        // Card title
        JLabel cardTitle = new JLabel("Sign In");
        cardTitle.setFont(new Font("Inter", Font.BOLD, 32));
        cardTitle.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginCard.add(cardTitle, gbc);
        
        // Email field
        gbc.gridwidth = 1;
        gbc.insets = new Insets(30, 30, 5, 30);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        emailLabel.setForeground(TEXT_DARK);
        loginCard.add(emailLabel, gbc);
        
        emailField = new JTextField();
        styleAppleTextField(emailField);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 20, 30);
        loginCard.add(emailField, gbc);
        
        // Password field
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(5, 30, 5, 30);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        passwordLabel.setForeground(TEXT_DARK);
        loginCard.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField();
        styleAppleTextField(passwordField);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 30, 30);
        loginCard.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Sign In");
        styleModernPrimaryButton(loginButton);
        loginButton.addActionListener(e -> performLogin());
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginCard.add(loginButton, gbc);
        
        // Register button
        JButton registerButton = new JButton("Create Account");
        styleModernSecondaryButton(registerButton);
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 30, 20, 30);
        loginCard.add(registerButton, gbc);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        statusLabel.setForeground(ERROR_RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 30, 10, 30);
        loginCard.add(statusLabel, gbc);
        
        cardContainer.add(loginCard);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(heroPanel, BorderLayout.CENTER);
        panel.add(cardContainer, BorderLayout.SOUTH);
        return panel;
    }
    
    private JLabel createBadge(String text, Color backgroundColor) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Inter", Font.PLAIN, 14));
        badge.setForeground(TEXT_DARK);
        badge.setBackground(backgroundColor);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(8, 16, 8, 16));
        badge.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(backgroundColor, 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        return badge;
    }
    
    private JPanel createModernCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(280, 180));
        return card;
    }
    
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(APPLE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(280, 180));
        return card;
    }
    
    private void styleAppleTextField(JTextField field) {
        field.setFont(new Font("Inter", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(CARD_WHITE);
        field.setForeground(TEXT_DARK);
        field.setPreferredSize(new Dimension(0, 48));
    }
    
    private void styleModernPrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(CARD_WHITE);
        button.setBorder(new EmptyBorder(14, 24, 14, 24));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 48));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(79, 70, 229));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_BLUE);
            }
        });
    }
    
    private void styleModernSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 16));
        button.setBackground(BACKGROUND_LIGHT);
        button.setForeground(TEXT_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 48));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(243, 244, 246));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_LIGHT);
            }
        });
    }
    
    private void styleApplePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBackground(APPLE_BLUE);
        button.setForeground(APPLE_WHITE);
        button.setBorder(new EmptyBorder(14, 24, 14, 24));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 48));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(79, 70, 229));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(APPLE_BLUE);
            }
        });
    }
    
    private void styleAppleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 16));
        button.setBackground(APPLE_SUPER_LIGHT_GRAY);
        button.setForeground(APPLE_DARK_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 48));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(243, 244, 246));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(APPLE_SUPER_LIGHT_GRAY);
            }
        });
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APPLE_SUPER_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(APPLE_WHITE);
        headerPanel.setBorder(new EmptyBorder(40, 0, 40, 0));
        
        JLabel headerTitle = new JLabel("Join Codevia");
        headerTitle.setFont(new Font("SF Pro Display", Font.BOLD, 48));
        headerTitle.setForeground(APPLE_DARK_GRAY);
        headerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        
        // Registration card
        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setBackground(APPLE_SUPER_LIGHT_GRAY);
        cardContainer.setBorder(new EmptyBorder(20, 40, 60, 40));
        
        JPanel regCard = createCard();
        regCard.setLayout(new GridBagLayout());
        regCard.setPreferredSize(new Dimension(450, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 10, 30);
        
        // Card title
        JLabel cardTitle = new JLabel("Create Account");
        cardTitle.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        cardTitle.setForeground(APPLE_DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        regCard.add(cardTitle, gbc);
        
        // Username field
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 30, 5, 30);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 15));
        usernameLabel.setForeground(APPLE_DARK_GRAY);
        regCard.add(usernameLabel, gbc);
        
        usernameField = new JTextField();
        styleAppleTextField(usernameField);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 15, 30);
        regCard.add(usernameField, gbc);
        
        // Email field
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(5, 30, 5, 30);
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 15));
        emailLabel.setForeground(APPLE_DARK_GRAY);
        regCard.add(emailLabel, gbc);
        
        JTextField regEmailField = new JTextField();
        styleAppleTextField(regEmailField);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 15, 30);
        regCard.add(regEmailField, gbc);
        
        // Password field
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(5, 30, 5, 30);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 15));
        passwordLabel.setForeground(APPLE_DARK_GRAY);
        regCard.add(passwordLabel, gbc);
        
        JPasswordField regPasswordField = new JPasswordField();
        styleAppleTextField(regPasswordField);
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 25, 30);
        regCard.add(regPasswordField, gbc);
        
        // Register button
        JButton registerButton = new JButton("Create Account");
        styleApplePrimaryButton(registerButton);
        registerButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String email = regEmailField.getText().trim();
                String password = new String(regPasswordField.getPassword());

                currentUser = userService.createUser(username, email, password);
                JOptionPane.showMessageDialog(this, "Registration successful! Welcome " + username + "!");
                cardLayout.show(mainPanel, "LOGIN");

            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        regCard.add(registerButton, gbc);
        
        // Back button
        JButton backButton = new JButton("Back to Sign In");
        styleAppleSecondaryButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 30, 20, 30);
        regCard.add(backButton, gbc);
        
        cardContainer.add(regCard);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Navigation bar
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(APPLE_WHITE);
        navPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JLabel logoLabel = new JLabel("Codevia");
        logoLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        logoLabel.setForeground(APPLE_DARK_GRAY);
        navPanel.add(logoLabel, BorderLayout.WEST);
        
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        userInfoLabel.setForeground(APPLE_GRAY);
        navPanel.add(userInfoLabel, BorderLayout.EAST);

        // Hero section
        JPanel heroSection = new JPanel(new BorderLayout());
        heroSection.setBackground(APPLE_WHITE);
        heroSection.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        JLabel welcomeTitle = new JLabel("Welcome back!");
        welcomeTitle.setFont(new Font("SF Pro Display", Font.BOLD, 48));
        welcomeTitle.setForeground(APPLE_DARK_GRAY);
        welcomeTitle.setHorizontalAlignment(SwingConstants.CENTER);
        heroSection.add(welcomeTitle, BorderLayout.CENTER);
        
        JLabel subtitle = new JLabel("Continue your learning journey");
        subtitle.setFont(new Font("SF Pro Text", Font.PLAIN, 20));
        subtitle.setForeground(APPLE_GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setBorder(new EmptyBorder(20, 0, 0, 0));
        heroSection.add(subtitle, BorderLayout.SOUTH);

        // Menu cards
        JPanel cardsContainer = new JPanel(new GridBagLayout());
        cardsContainer.setBackground(APPLE_SUPER_LIGHT_GRAY);
        cardsContainer.setBorder(new EmptyBorder(40, 40, 80, 40));
        cardsContainer.setPreferredSize(new Dimension(0, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Skills card
        JPanel skillsCard = createMenuCard("Skills", "Master programming concepts", APPLE_BLUE, "📚");
        skillsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "SKILLS");
            }
        });
        gbc.gridx = 0; gbc.gridy = 0;
        cardsContainer.add(skillsCard, gbc);
        
        // Quiz card
        JPanel quizCard = createMenuCard("Quizzes", "Test your knowledge", APPLE_GREEN, "🎯");
        quizCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "QUIZ_SELECTION");
            }
        });
        gbc.gridx = 1; gbc.gridy = 0;
        cardsContainer.add(quizCard, gbc);
        
        // Achievements card
        JPanel achievementsCard = createMenuCard("Achievements", "View your progress", APPLE_ORANGE, "🏆");
        achievementsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "ACHIEVEMENTS");
            }
        });
        gbc.gridx = 0; gbc.gridy = 1;
        cardsContainer.add(achievementsCard, gbc);
        
        // Logout card
        JPanel logoutCard = createMenuCard("Sign Out", "End your session", APPLE_RED, "🚪");
        logoutCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                currentUser = null;
                cardLayout.show(mainPanel, "LOGIN");
            }
        });
        gbc.gridx = 1; gbc.gridy = 1;
        cardsContainer.add(logoutCard, gbc);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(heroSection, BorderLayout.CENTER);
        panel.add(cardsContainer, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createMenuCard(String title, String description, Color accentColor, String emoji) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(APPLE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(280, 180));
        card.setMaximumSize(new Dimension(280, 180));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(APPLE_SUPER_LIGHT_GRAY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(APPLE_WHITE);
            }
        });
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Icon
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(APPLE_DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        descLabel.setForeground(APPLE_GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        content.add(iconLabel, BorderLayout.NORTH);
        content.add(titleLabel, BorderLayout.CENTER);
        content.add(descLabel, BorderLayout.SOUTH);
        
        card.add(content, BorderLayout.CENTER);
        
        // Accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(accentColor);
        accentLine.setPreferredSize(new Dimension(0, 4));
        card.add(accentLine, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createSkillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(APPLE_WHITE);
        navPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JButton backButton = new JButton("← Back");
        styleAppleSecondaryButton(backButton);
        backButton.addActionListener(e -> {
            refreshSkillsDisplay();
            updateUserInfo();
            cardLayout.show(mainPanel, "MAIN_MENU");
        });
        navPanel.add(backButton, BorderLayout.WEST);
        
        JLabel navTitle = new JLabel("Skills");
        navTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        navTitle.setForeground(APPLE_DARK_GRAY);
        navPanel.add(navTitle, BorderLayout.CENTER);

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(APPLE_WHITE);
        headerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel titleLabel = new JLabel("Master Your Skills");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        titleLabel.setForeground(APPLE_DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Unlock programming concepts as you progress");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
        subtitleLabel.setForeground(APPLE_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Skills container
        skillsContainer = new JPanel();
        skillsContainer.setLayout(new BoxLayout(skillsContainer, BoxLayout.Y_AXIS));
        skillsContainer.setBackground(APPLE_SUPER_LIGHT_GRAY);
        skillsContainer.setBorder(new EmptyBorder(30, 40, 60, 40));
        
        refreshSkillsDisplay();
        
        JScrollPane scrollPane = new JScrollPane(skillsContainer);
        scrollPane.setBackground(APPLE_SUPER_LIGHT_GRAY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.getViewport().setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Main content panel that takes remaining space
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(APPLE_SUPER_LIGHT_GRAY);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(headerPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void refreshSkillsDisplay() {
        if (skillsContainer != null) {
            skillsContainer.removeAll();
            
            List<Skill> skills = skillService.getSkills();
            for (int i = 0; i < skills.size(); i++) {
                Skill skill = skills.get(i);
                JPanel skillCard = createSkillCard(skill, i);
                skillsContainer.add(skillCard);
                
                if (i < skills.size() - 1) {
                    skillsContainer.add(Box.createVerticalStrut(15));
                }
            }
            
            skillsContainer.revalidate();
            skillsContainer.repaint();
        }
    }
    
    private JPanel createSkillCard(Skill skill, int index) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(APPLE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        card.setPreferredSize(new Dimension(0, 120));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setMinimumSize(new Dimension(200, 120));
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Left section - icon and info
        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);
        
        JLabel iconLabel = new JLabel(skill.isUnlocked() ? "✓" : "🔒");
        iconLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        iconLabel.setForeground(skill.isUnlocked() ? APPLE_GREEN : APPLE_GRAY);
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel nameLabel = new JLabel(skill.getSkillName());
        nameLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        nameLabel.setForeground(APPLE_DARK_GRAY);
        
        JLabel descLabel = new JLabel(skill.getDescription());
        descLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        descLabel.setForeground(APPLE_GRAY);
        
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(descLabel, BorderLayout.SOUTH);
        
        leftSection.add(iconLabel, BorderLayout.WEST);
        leftSection.add(infoPanel, BorderLayout.CENTER);
        
        // Right section - status and action
        JPanel rightSection = new JPanel(new BorderLayout());
        rightSection.setOpaque(false);
        
        if (skill.isUnlocked()) {
            JLabel statusLabel = new JLabel("Unlocked");
            statusLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
            statusLabel.setForeground(APPLE_GREEN);
            rightSection.add(statusLabel, BorderLayout.CENTER);
        } else {
            JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
            
            JLabel xpLabel = new JLabel("Requires " + skill.getRequiredXP() + " XP");
            xpLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
            xpLabel.setForeground(APPLE_GRAY);
            xpLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton unlockButton = new JButton("Unlock");
            unlockButton.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
            unlockButton.setBackground(APPLE_BLUE);
            unlockButton.setForeground(APPLE_WHITE);
            unlockButton.setBorder(new EmptyBorder(8, 16, 8, 16));
            unlockButton.setFocusPainted(false);
            unlockButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            unlockButton.setOpaque(true);
            
            // Check if user has enough XP
            if (currentUser == null || currentUser.getExperiencePoint() < skill.getRequiredXP()) {
                unlockButton.setEnabled(false);
                unlockButton.setBackground(APPLE_LIGHT_GRAY);
                unlockButton.setForeground(APPLE_GRAY);
            }
            
            unlockButton.addActionListener(e -> {
                if (currentUser.getExperiencePoint() >= skill.getRequiredXP()) {
                    skill.setUnlocked(true);
                    currentUser.unlockSkill(skill.getSkillName());
                    refreshSkillsDisplay();
                    updateUserInfo();
                    
                    JOptionPane.showMessageDialog(this, 
                        "🎉 Skill Unlocked!\n\n" + skill.getSkillName() + " is now available.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Not enough XP to unlock this skill.\n\nYou need " + skill.getRequiredXP() + " XP but only have " + currentUser.getExperiencePoint() + " XP.", 
                        "Insufficient XP", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            actionPanel.add(xpLabel, BorderLayout.NORTH);
            actionPanel.add(unlockButton, BorderLayout.SOUTH);
            rightSection.add(actionPanel, BorderLayout.CENTER);
        }
        
        content.add(leftSection, BorderLayout.CENTER);
        content.add(rightSection, BorderLayout.EAST);
        
        card.add(content, BorderLayout.CENTER);
        
        // Progress indicator
        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(skill.isUnlocked() ? APPLE_GREEN : APPLE_LIGHT_GRAY);
        progressPanel.setPreferredSize(new Dimension(0, 3));
        card.add(progressPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createQuizSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(APPLE_WHITE);
        navPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JButton backButton = new JButton("← Back");
        styleAppleSecondaryButton(backButton);
        backButton.addActionListener(e -> {
            updateUserInfo();
            cardLayout.show(mainPanel, "MAIN_MENU");
        });
        navPanel.add(backButton, BorderLayout.WEST);
        
        JLabel navTitle = new JLabel("Quizzes");
        navTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        navTitle.setForeground(APPLE_DARK_GRAY);
        navPanel.add(navTitle, BorderLayout.CENTER);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(APPLE_WHITE);
        headerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel titleLabel = new JLabel("Test Your Knowledge");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        titleLabel.setForeground(APPLE_DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Choose a quiz from your unlocked skills");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
        subtitleLabel.setForeground(APPLE_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Quiz cards container
        JPanel quizContainer = new JPanel();
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBackground(APPLE_SUPER_LIGHT_GRAY);
        quizContainer.setBorder(new EmptyBorder(30, 40, 60, 40));
        
        // Populate quizzes based on unlocked skills
        List<Skill> unlockedSkills = skillService.getSkills().stream()
            .filter(Skill::isUnlocked)
            .collect(java.util.stream.Collectors.toList());
            
        System.out.println("DEBUG: Found " + unlockedSkills.size() + " unlocked skills for quizzes");
        for (Skill skill : unlockedSkills) {
            System.out.println("DEBUG: Unlocked skill: " + skill.getSkillName());
        }
            
        if (unlockedSkills.isEmpty()) {
            JPanel emptyState = createCard();
            emptyState.setLayout(new BorderLayout());
            emptyState.setPreferredSize(new Dimension(0, 200));
            emptyState.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            
            JPanel emptyContent = new JPanel(new BorderLayout());
            emptyContent.setOpaque(false);
            emptyContent.setBorder(new EmptyBorder(40, 40, 40, 40));
            
            JLabel emptyIcon = new JLabel("🔒");
            emptyIcon.setFont(new Font("Apple Color Emoji", Font.PLAIN, 48));
            emptyIcon.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel emptyTitle = new JLabel("No Quizzes Available");
            emptyTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
            emptyTitle.setForeground(APPLE_DARK_GRAY);
            emptyTitle.setHorizontalAlignment(SwingConstants.CENTER);
            emptyTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
            
            JLabel emptyDesc = new JLabel("Unlock skills first to access quizzes");
            emptyDesc.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
            emptyDesc.setForeground(APPLE_GRAY);
            emptyDesc.setHorizontalAlignment(SwingConstants.CENTER);
            
            emptyContent.add(emptyIcon, BorderLayout.NORTH);
            emptyContent.add(emptyTitle, BorderLayout.CENTER);
            emptyContent.add(emptyDesc, BorderLayout.SOUTH);
            
            emptyState.add(emptyContent, BorderLayout.CENTER);
            quizContainer.add(emptyState);
        } else {
            for (int i = 0; i < unlockedSkills.size(); i++) {
                Skill skill = unlockedSkills.get(i);
                JPanel quizCard = createQuizCard(skill);
                quizContainer.add(quizCard);
                
                if (i < unlockedSkills.size() - 1) {
                    quizContainer.add(Box.createVerticalStrut(15));
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(quizContainer);
        scrollPane.setBackground(APPLE_SUPER_LIGHT_GRAY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.getViewport().setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Main content panel that takes remaining space
        JPanel quizContentPanel = new JPanel(new BorderLayout());
        quizContentPanel.setBackground(APPLE_SUPER_LIGHT_GRAY);
        quizContentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(headerPanel, BorderLayout.CENTER);
        panel.add(quizContentPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createQuizCard(Skill skill) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(APPLE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        card.setPreferredSize(new Dimension(0, 120));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setMinimumSize(new Dimension(200, 120));
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Left section
        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🎯");
        iconLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 24));
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel nameLabel = new JLabel(skill.getSkillName() + " Quiz");
        nameLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        nameLabel.setForeground(APPLE_DARK_GRAY);
        
        JLabel descLabel = new JLabel("Test your " + skill.getSkillName().toLowerCase() + " knowledge");
        descLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        descLabel.setForeground(APPLE_GRAY);
        
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(descLabel, BorderLayout.SOUTH);
        
        leftSection.add(iconLabel, BorderLayout.WEST);
        leftSection.add(infoPanel, BorderLayout.CENTER);
        
        // Right section
        JButton startButton = new JButton("Start Quiz");
        styleApplePrimaryButton(startButton);
        startButton.setPreferredSize(new Dimension(120, 44));
        startButton.addActionListener(e -> startQuiz(skill.getSkillName()));
        
        content.add(leftSection, BorderLayout.CENTER);
        content.add(startButton, BorderLayout.EAST);
        
        card.add(content, BorderLayout.CENTER);
        
        // Accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(APPLE_BLUE);
        accentLine.setPreferredSize(new Dimension(0, 3));
        card.add(accentLine, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_LIGHT);

        // Header panel (empty now, just for spacing)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30));
        headerPanel.setBackground(BACKGROUND_LIGHT);
        headerPanel.setPreferredSize(new Dimension(0, 60));

        // Quiz title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_LIGHT);
        titlePanel.setBorder(new EmptyBorder(30, 60, 40, 60));
        
        JLabel quizTitle = new JLabel("Codevia Learning Quiz");
        quizTitle.setFont(new Font("Inter", Font.BOLD, 48));
        quizTitle.setForeground(TEXT_DARK);
        quizTitle.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(quizTitle, BorderLayout.CENTER);
        
        // Score label
        scoreLabel = new JLabel("Score: 0/0");
        scoreLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        scoreLabel.setForeground(TEXT_GRAY);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(scoreLabel, BorderLayout.SOUTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_LIGHT);
        contentPanel.setBorder(new EmptyBorder(0, 60, 60, 60));
        contentPanel.setPreferredSize(new Dimension(0, 500));
        
        // Question section
        JPanel questionSection = new JPanel();
        questionSection.setLayout(new BoxLayout(questionSection, BoxLayout.Y_AXIS));
        questionSection.setBackground(BACKGROUND_LIGHT);
        
        // Question label
        currentQuestionLabel = new JLabel("");
        currentQuestionLabel.setFont(new Font("Inter", Font.BOLD, 24));
        currentQuestionLabel.setForeground(TEXT_DARK);
        currentQuestionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentQuestionLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        questionSection.add(currentQuestionLabel);
        
        // Instruction label
        JLabel instructionLabel = new JLabel("Choose only 1 answer:");
        instructionLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        instructionLabel.setForeground(TEXT_GRAY);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        questionSection.add(instructionLabel);
        
        // Answer options container
        JPanel answersContainer = new JPanel();
        answersContainer.setLayout(new BoxLayout(answersContainer, BoxLayout.Y_AXIS));
        answersContainer.setBackground(BACKGROUND_LIGHT);
        answersContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Answer options
        answerOptions = new JRadioButton[4];
        answerGroup = new ButtonGroup();
        
        for (int i = 0; i < 4; i++) {
            JPanel optionCard = createAnswerOptionCard(i);
            answersContainer.add(optionCard);
            if (i < 3) {
                answersContainer.add(Box.createVerticalStrut(12));
            }
        }
        
        questionSection.add(answersContainer);
        
        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 30));
        buttonPanel.setBackground(BACKGROUND_LIGHT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton backButton = new JButton("← Back to Quizzes");
        styleModernSecondaryButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "QUIZ_SELECTION"));
        buttonPanel.add(backButton);
        
        buttonPanel.add(Box.createHorizontalStrut(15));
        
        nextButton = new JButton("Next");
        styleModernPrimaryButton(nextButton);
        nextButton.addActionListener(e -> {
            System.out.println("DEBUG: Next button clicked!");
            handleNextQuestion();
        });
        buttonPanel.add(nextButton);
        
        questionSection.add(buttonPanel);
        
        contentPanel.add(questionSection, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createAchievementsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(APPLE_WHITE);
        navPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JButton backButton = new JButton("← Back");
        styleAppleSecondaryButton(backButton);
        backButton.addActionListener(e -> {
            updateUserInfo();
            cardLayout.show(mainPanel, "MAIN_MENU");
        });
        navPanel.add(backButton, BorderLayout.WEST);
        
        JLabel navTitle = new JLabel("Achievements");
        navTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        navTitle.setForeground(APPLE_DARK_GRAY);
        navPanel.add(navTitle, BorderLayout.CENTER);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(APPLE_WHITE);
        headerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel titleLabel = new JLabel("Your Achievements");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        titleLabel.setForeground(APPLE_DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Track your learning progress and milestones");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
        subtitleLabel.setForeground(APPLE_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Achievements content
        JPanel achievementsContainer = new JPanel();
        achievementsContainer.setLayout(new BoxLayout(achievementsContainer, BoxLayout.Y_AXIS));
        achievementsContainer.setBackground(APPLE_SUPER_LIGHT_GRAY);
        achievementsContainer.setBorder(new EmptyBorder(30, 40, 60, 40));
        
        // Sample achievements
        String[] achievements = {
            "🎯 First Quiz Completed",
            "📚 Java Basics Mastered",
            "⭐ 100 XP Earned",
            "🔥 3-Day Streak",
            "🏆 Quiz Champion"
        };
        
        for (String achievement : achievements) {
            JPanel achievementCard = createAchievementCard(achievement);
            achievementsContainer.add(achievementCard);
            achievementsContainer.add(Box.createVerticalStrut(15));
        }
        
        JScrollPane scrollPane = new JScrollPane(achievementsContainer);
        scrollPane.setBackground(APPLE_SUPER_LIGHT_GRAY);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(APPLE_SUPER_LIGHT_GRAY);

        // Content panel that takes remaining space
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(APPLE_SUPER_LIGHT_GRAY);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(headerPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createAchievementCard(String achievementText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(APPLE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        card.setPreferredSize(new Dimension(0, 80));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel achievementLabel = new JLabel(achievementText);
        achievementLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 18));
        achievementLabel.setForeground(APPLE_DARK_GRAY);
        
        card.add(achievementLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAnswerOptionCard(int index) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Create radio button with custom styling
        answerOptions[index] = new JRadioButton("");
        answerOptions[index].setFont(new Font("Inter", Font.PLAIN, 16));
        answerOptions[index].setForeground(TEXT_DARK);
        answerOptions[index].setBackground(CARD_WHITE);
        answerOptions[index].setOpaque(false);
        answerOptions[index].setBorderPainted(false);
        answerOptions[index].setFocusPainted(false);
        answerGroup.add(answerOptions[index]);
        
        // Add click handler to the entire card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                answerOptions[index].setSelected(true);
                updateAnswerCardStyles();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!answerOptions[index].isSelected()) {
                    card.setBackground(new Color(248, 250, 252));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!answerOptions[index].isSelected()) {
                    card.setBackground(CARD_WHITE);
                }
            }
        });
        
        card.add(answerOptions[index], BorderLayout.CENTER);
        return card;
    }
    
    private void updateAnswerCardStyles() {
        // Update card styling based on selection state
        for (int i = 0; i < answerOptions.length; i++) {
            if (answerOptions[i] != null && answerOptions[i].getParent() != null) {
                JPanel card = (JPanel) answerOptions[i].getParent();
                if (answerOptions[i].isSelected()) {
                    card.setBackground(LIGHT_BLUE);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_BLUE, 2, true),
                        new EmptyBorder(19, 19, 19, 19)
                    ));
                } else {
                    card.setBackground(CARD_WHITE);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_LIGHT, 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                    ));
                }
            }
        }
    }
    
    private void startQuiz(String skillName) {
        System.out.println("DEBUG: Starting quiz for skill: " + skillName);
        
        // Find the quiz for the selected skill
        if (skillName.equals("Java Basics")) {
            currentQuiz = createJavaBasicsQuiz();
        } else if (skillName.equals("OOP Concepts")) {
            currentQuiz = createOOPQuiz();
        } else {
            // Create a default quiz for other skills
            currentQuiz = createDefaultQuiz(skillName);
        }
        
        if (currentQuiz == null) {
            System.out.println("DEBUG: Quiz is null for skill: " + skillName);
            JOptionPane.showMessageDialog(this, "Quiz not available for this skill yet.");
            return;
        }
        
        System.out.println("DEBUG: Quiz created successfully with " + currentQuiz.getQuestions().size() + " questions");
        
        currentQuestionIndex = 0;
        score = 0;
        
        // Check if scoreLabel exists
        if (scoreLabel != null) {
            scoreLabel.setText("Score: 0/" + currentQuiz.getQuestions().size());
        } else {
            System.out.println("DEBUG: scoreLabel is null!");
        }
        
        showQuestion();
        cardLayout.show(mainPanel, "QUIZ");
    }
    
    private Quiz createDefaultQuiz(String skillName) {
        // Create a simple quiz for any skill
        List<Question> questions = List.of(
            new Question("This is a sample question for " + skillName + ". What is the answer?", 
                List.of("Option A", "Option B", "Option C", "Option D"), 1),
            new Question("Another question about " + skillName + ". Choose the best answer.", 
                List.of("First choice", "Second choice", "Third choice", "Fourth choice"), 2)
        );
        return new Quiz(skillName.toLowerCase().replace(" ", "-"), skillName, questions, 70);
    }
    
    private Quiz createJavaBasicsQuiz() {
        List<Question> questions = List.of(
            new Question("What is the size of int in Java?", 
                List.of("2 bytes", "4 bytes", "8 bytes", "16 bytes"), 2),
            new Question("Which loop checks the condition after executing once?", 
                List.of("for", "while", "do-while", "foreach"), 3)
        );
        return new Quiz("java-basics", "Java Basics", questions, 70);
    }
    
    private Quiz createOOPQuiz() {
        List<Question> questions = List.of(
            new Question("What is inheritance?", 
                List.of("Copying code", "A class deriving properties from another", "Unrelated classes", "None of the above"), 2),
            new Question("Which keyword is used to inherit a class in Java?", 
                List.of("inherits", "extends", "implements", "super"), 2)
        );
        return new Quiz("oop", "OOP", questions, 70);
    }
    
    private void showQuestion() {
        System.out.println("DEBUG: Showing question " + (currentQuestionIndex + 1) + " of " + currentQuiz.getQuestions().size());
        
        if (currentQuestionIndex < currentQuiz.getQuestions().size()) {
            Question question = currentQuiz.getQuestions().get(currentQuestionIndex);
            System.out.println("DEBUG: Question text: " + question.getQuestionText());
            
            currentQuestionLabel.setText("Question " + (currentQuestionIndex + 1) + ": " + question.getQuestionText());
            
            List<String> options = question.getOptions();
            System.out.println("DEBUG: Number of options: " + options.size());
            
            for (int i = 0; i < answerOptions.length; i++) {
                if (i < options.size()) {
                    answerOptions[i].setText(options.get(i));
                    answerOptions[i].setVisible(true);
                    System.out.println("DEBUG: Option " + (i+1) + ": " + options.get(i));
                } else {
                    answerOptions[i].setVisible(false);
                }
            }
            
            answerGroup.clearSelection();
            updateAnswerCardStyles();
            nextButton.setText(currentQuestionIndex == currentQuiz.getQuestions().size() - 1 ? "Finish" : "Next");
        } else {
            System.out.println("DEBUG: No more questions to show");
        }
    }
    
    private void handleNextQuestion() {
        System.out.println("DEBUG: handleNextQuestion called");
        
        // Check if an answer is selected
        int selectedAnswer = -1;
        for (int i = 0; i < answerOptions.length; i++) {
            if (answerOptions[i] != null && answerOptions[i].isSelected()) {
                selectedAnswer = i + 1;
                System.out.println("DEBUG: Selected answer: " + selectedAnswer);
                break;
            }
        }
        
        if (selectedAnswer == -1) {
            JOptionPane.showMessageDialog(this, "Please select an answer before continuing.", 
                "No Answer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if answer is correct
        Question currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);
        if (selectedAnswer == currentQuestion.getCorrectAnswer()) {
            score++;
        }
        
        currentQuestionIndex++;
        scoreLabel.setText("Score: " + score + "/" + currentQuiz.getQuestions().size());
        
        if (currentQuestionIndex < currentQuiz.getQuestions().size()) {
            showQuestion();
        } else {
            // Quiz finished
            int earnedXP = score * 20;
            if (currentUser != null) {
                currentUser.addExperience(earnedXP);
            }
            
            String message = String.format(
                "🎉 Quiz Completed!\n\n" +
                "Final Score: %d/%d (%.1f%%)\n" +
                "XP Earned: +%d\n\n" +
                "Great job on completing the %s quiz!",
                score, currentQuiz.getQuestions().size(),
                (score * 100.0 / currentQuiz.getQuestions().size()),
                earnedXP, currentQuiz.getSkillName()
            );
            
            JOptionPane.showMessageDialog(this, message, "Quiz Complete", JOptionPane.INFORMATION_MESSAGE);
            
            // Update user info and return to main menu
            updateUserInfo();
            cardLayout.show(mainPanel, "MAIN_MENU");
        }
    }


    // This method is no longer used but kept for compatibility
    private JButton createMenuButton(String text, String targetPanel) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 80));
        styleApplePrimaryButton(button);
        button.addActionListener(e -> cardLayout.show(mainPanel, targetPanel));
        return button;
    }

    private void performLogin() {
        try {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            System.out.println("DEBUG: Attempting login with email: " + email);

            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                return;
            }

            // Mock authentication for demo
            currentUser = userService.findUserByEmail(email);
            if (currentUser == null) {
                statusLabel.setText("User not found");
                System.out.println("DEBUG: User not found for email: " + email);
                return;
            }

            if (!currentUser.getPassword().equals(password)) {
                statusLabel.setText("Invalid password");
                System.out.println("DEBUG: Invalid password for user: " + email);
                return;
            }

            System.out.println("DEBUG: Login successful for user: " + currentUser.getUsername());
            System.out.println("DEBUG: User XP: " + currentUser.getExperiencePoint());
            System.out.println("DEBUG: User Level: " + currentUser.getUserLevel());

            // Initialize achievement service for logged in user
            achievementService = new AchievementService(currentUser);

            statusLabel.setText("Login successful!");
            updateUserInfo();
            cardLayout.show(mainPanel, "MAIN_MENU");

            // Clear fields
            emailField.setText("");
            passwordField.setText("");
            statusLabel.setText(" ");

        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
            System.out.println("DEBUG: Login exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText("Welcome, " + currentUser.getUsername() +
                    " | Level: " + currentUser.getUserLevel() +
                    " | XP: " + currentUser.getExperiencePoint());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new CodeviaGUI().setVisible(true);
        });
    }
}