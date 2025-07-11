package service;

import model.Question;
import model.Quiz;
import model.User;
import model.DailyChallenge;

import java.util.*;

public class QuizService {
    // A map to store quizzes with the skill name as the key
    private Map<String, Quiz> quizMap = new HashMap<>();
    private DailyChallengeService challengeService;



    // Constructor initializes sample quizzes.

    public QuizService() {
        createSampleQuizzes();
    }


    private void createSampleQuizzes() {
        // Quiz for "Java Basics"
        List<Question> javaQuestions = List.of(
                new Question("What is the size of int in Java?",
                        Arrays.asList("2 bytes", "4 bytes", "8 bytes"), 1), // Correct: "4 bytes"
                new Question("Which loop checks the condition *after* executing once?",
                        Arrays.asList("for", "while", "do-while"), 2) // Correct: "do-while"
        );

        // Quiz for "OOP"
        List<Question> oopQuestions = List.of(
                new Question("What is inheritance?",
                        Arrays.asList(
                                "Copying code from one class to another",
                                "A class deriving properties from another", // Correct
                                "Unrelated class sharing names"
                        ), 1),
                new Question("Which keyword is used to inherit a class in Java?",
                        Arrays.asList("inherits", "extends", "implements"), 1) // Correct: "extends"
        );

        // Map each quiz to its related skill
        quizMap.put("Java Basics", new Quiz("java-basics", "Java Basics", javaQuestions, 70));
        quizMap.put("OOP", new Quiz("oop", "OOP", oopQuestions, 70));
    }

    /**
     * Starts a quiz for the given skill name.
     * Displays questions, collects user input, and awards XP based on performance.
     */
    public void startQuiz(String skillName, Scanner scanner, User user, AchievementService achievementService, DailyChallengeService challengeService) {
        // Retrieve the quiz by skill name
        Quiz quiz = quizMap.get(skillName);
        if (quiz == null) {
            System.out.println("❌ No quiz available for this skill.");
            return;
        }

        System.out.println("🧪 Starting Quiz: " + skillName);
        int score = 0; // Track number of correct answers

        // Loop through each question in the quiz
        for (Question q : quiz.getQuestions()) {
            q.display(); // Display question and options
            System.out.print("Your answer: ");

            int answer;
            try {
                answer = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Skipping question.\n");
                continue;
            }

            // Check the answer
            if (q.checkAnswer(answer)) {
                System.out.println("✅ Correct!\n");
                score++;
            } else {
                System.out.println("❌ Incorrect.\n");
            }

            challengeService.trackQuestionAnswered(user, achievementService);
            achievementService.quizCompleted();

        }

        // Calculate XP earned (20 XP per correct answer)
        int earnedXp = score * 20;

        // Reward XP to user and possibly level them up
        user.gainExperiencePoint(earnedXp); // Make sure your User class uses this method name

        // Final score report
        System.out.println("🎉 You scored " + score + "/" + quiz.getQuestions().size() +
                " and earned " + earnedXp + " XP!");

        achievementService.quizCompleted();
    }
    
    public Quiz getQuizBySkillName(String skillName) {
        return quizMap.get(skillName);
    }

}


