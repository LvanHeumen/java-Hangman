// Hangman: an implementation of the classic Hangman game in Java.

// Importing libraries
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.*;
import java.util.Scanner;


public class Hangman{
  // Scanners and data that are used in methods that are called more often.
  static private Scanner keyboardInput = new Scanner(System.in);
  static private ArrayList<Character> usedLetters = new ArrayList<Character>();
  static private int turns;
  static private String secretWord;
  static private char[] displayWord;

  // Regex pattern
  static private Pattern upperCaseAlphabet = Pattern.compile("[A-Z]");

  static ArrayList<String> readWordFile(){
    ArrayList<String> wordList = new ArrayList<String>();
    try {
      File wordFile = new File("Odgens_basic.txt");
      Scanner wordReader = new Scanner(wordFile).useDelimiter("\\s*,\\s*");
      while (wordReader.hasNext()) {
        wordList.add(wordReader.next().toUpperCase());
      }
      wordReader.close();
    } catch(Exception e) {
      System.out.println("An error occurred accessing the file!");
      e.printStackTrace();
    }
    return wordList;
  }

  static String selectRandomWord(ArrayList<String> wordList){
        Random randomiser = new Random();
        System.out.println(wordList.size());
        String randomWord = wordList.get(randomiser.nextInt(wordList.size()));
        return randomWord;
  }

  // Method to set up the required variables to start a game
  static void setGameVariables(){
    // Set a 'display word'
    displayWord = new char[secretWord.length()];
    Arrays.fill(displayWord, '_');

    // Dynamic turns, player gets a max of twice the word length.
    turns = 2*secretWord.length();

    // Welcome message.
    System.out.println("Welcome to Hangman! Your secret word is: ");
    displayGameState();
  }

  // Method to dislay the current Game State: Process of secret word + guessed letters.
  static void displayGameState(){
    for (int i = 0; i < displayWord.length; i++ ) {
        System.out.print(displayWord[i] + " ");
    }
    System.out.println("\tGuessed letters: " + usedLetters + "\tTurns left: " + turns);
  }

  // Method to request and check user input. Also checks if user input is (in) the secret word.
  static void checkInput(){
    char chosenLetter;
    String enteredWord;
    boolean inWord = false;
    boolean guessedLetter = false;

    do {
      System.out.println("Please choose a letter! Alternatively, you can guess the word!");
      enteredWord = keyboardInput.nextLine().toUpperCase(); // Reads input, sets to upper case.
      chosenLetter = enteredWord.charAt(0);

      if (!enteredWord.equals(secretWord)) {
        if (enteredWord.length() != 1) {                    // Checks if more than one letter was entered
          System.out.println("Sorry, the word wasn't " + enteredWord);
          break;
        }
        else {
          guessedLetter = true;
        }
      }
      else {                                              // The secret word has been guessed! First implementation, fill out displayWord
        for (int i = 0; i < secretWord.length() ; i++) {
          displayWord[i] = secretWord.charAt(i);
        }
        break;
      }


      // Regex check for valid input
      Matcher checker = upperCaseAlphabet.matcher(String.valueOf(chosenLetter));
      boolean validLetter = checker.find();

      // Check against regex and if character hasn't been input before, if not, break out of the loop
      if (!validLetter) {
        System.out.println("Invalid character, please enter a letter of the alphabet.");
      }
      else if (usedLetters.contains(chosenLetter)) {
        System.out.println("You have already entered that letter before, please choose a new one!");
      }
      else {
        break;
      }
    } while (true);

    // If an actual letter was guessed, check against secret word.
    if (guessedLetter) {
      usedLetters.add(chosenLetter);

      for (int i = 0; i < secretWord.length() ; i++ ) {
        if (secretWord.charAt(i) == chosenLetter) {
          displayWord[i] = chosenLetter;
          inWord = true;
        }
      }

      if (!inWord) {
        System.out.println(chosenLetter + " was not in the word");
      }
    }

    //Update turn number
    turns--;
  }

  static boolean isWinner(){
    boolean winner = false;

    for (int i = 0; i < secretWord.length(); i++) {
      if (displayWord[i] != secretWord.charAt(i)) {
        break;
      }
      else if (i == secretWord.length()-1) {
        winner = true;
      }
    }
    return winner;
  }

  static boolean askForRestart(){
    char goAgain;
    boolean rePlay;

    do {
      System.out.println("Would you like to play again? Y/N");
      goAgain = keyboardInput.nextLine().toUpperCase().charAt(0);

      if (goAgain == 'Y') {
        rePlay = true;
        break;
      }
      else if (goAgain == 'N') {
        rePlay = false;
        break;
      }
      else {
        System.out.println("Sorry, I didn't quite understand that");
      }

    } while (true);

    return rePlay;
  }

  public static void main(String[] args) {
    // Define variables that only need to be called once
    ArrayList<String> wordList = new ArrayList<String>();
    boolean rePlay = false;
    boolean gameOver = false;

    // Acquire word list from file
    wordList = readWordFile();

    // Pull a new secret word from the word list
    secretWord = selectRandomWord(wordList);

    // Call the method to set the game variables
    setGameVariables();
    // The actual game takes place here
    while (turns > 0) {
      checkInput();

      displayGameState();

      boolean hasWon = isWinner();

      if (hasWon) {
        System.out.println("Congratulations, you guessed the word! It was " +secretWord);
        gameOver = true;
        rePlay = askForRestart();
      }
      else if (turns == 0) {
        System.out.println("Unfortunately, you didn't manage to guess: " +secretWord);
        rePlay = askForRestart();
      }

      if (gameOver && rePlay) {
        secretWord = selectRandomWord(wordList);              // Selects a new word and resets the turn variable
        setGameVariables();
        gameOver = false;
        rePlay = false;
      }
      else if (gameOver && !rePlay) {
        break;                                                // Else the game technically continues if the word has been guessed.
      }
    }
  }
}
