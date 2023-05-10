package application;

import java.io.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Interface extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Project 2");
		// Create the load and back buttons
		Button loadButton = new Button("Load");
		Button backButton = new Button("Back");

		// Create the text area in between the back and load buttons
		TextArea topTextArea = new TextArea();
		topTextArea.setPrefColumnCount(20);
		topTextArea.setPrefRowCount(1);

		// Create the HBox to hold the buttons and text area
		HBox hbox = new HBox(10);
		hbox.setPadding(new Insets(10));
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().addAll(backButton, topTextArea, loadButton);

		// Create the large text area in the center of the border pane
		Label centerLabel = new Label("Equations");
		TextArea centerTextArea = new TextArea();
		centerTextArea.setPrefColumnCount(40);
		centerTextArea.setPrefRowCount(10);

		// Create the smaller text area below the center text area
		Label bottomLabel = new Label("Files");
		TextArea bottomTextArea = new TextArea();
		bottomTextArea.setPrefColumnCount(40);
		bottomTextArea.setPrefRowCount(5);

		// Create a VBox to hold the center and bottom text areas
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));
		vbox.getChildren().addAll(centerLabel, centerTextArea, bottomLabel, bottomTextArea);

		// Create a stack to store the files that have been opened
		Stack fileStack = new Stack();

		// Create a FileChooser object to allow the user to select a file
		FileChooser fileChooser = new FileChooser();

		// Set the initial directory for the FileChooser
		File initialDirectory = new File("C:\\Users\\rami pc\\eclipse-workspace\\DSProject2");
		fileChooser.setInitialDirectory(initialDirectory);

		// Set the action for the load button
		loadButton.setOnAction(e -> {
			// Show the FileChooser and store the selected file
			File selectedFile = fileChooser.showOpenDialog(primaryStage);

			// Push the selected file onto the stack
			fileStack.push(selectedFile);

			// Try to display the contents of the selected file in the text areas
			try {
				display(selectedFile, topTextArea, centerTextArea, bottomTextArea);
			} catch (NullPointerException E) {
				// If no file was selected, display an error message
				topTextArea.appendText("\nno file was selected\n");
			}

			// If a file was selected and displayed, enable the back button
			if (!topTextArea.getText().equals("no file is selected\n")) {
				backButton.setDisable(false);
				backButton.setStyle(null);
			}
		});

		// Set the action for the back button
		backButton.setOnAction(e -> {
			// Pop the top file off the stack
			fileStack.pop();

			// Get the previously opened file from the stack
			File openedFile = (File) fileStack.pop();

			// Try to display the contents of the file in the text areas
			try {
				// Clear the center and bottom text areas before displaying the new file
				centerTextArea.clear();
				bottomTextArea.clear();
				display(openedFile, topTextArea, centerTextArea, bottomTextArea);
			} catch (NullPointerException E) {
				// If no file is selected, display an error message
				topTextArea.setText("no file is selected\n");
			}

			// If no file is selected, disable the back button
			while (topTextArea.getText().equals("no file is selected\n")) {
				backButton.setDisable(true);
				backButton.setStyle("-fx-backGround-color: grey;");
				break;
			}
		});

		// Create a stack to store the files that have been clicked on in the bottom
		// text area
		Stack reOpenFilesStack = new Stack();

		// Set the action for clicking on a file in the bottom text area
		bottomTextArea.setOnMouseClicked(e -> {
			// Get the file path of the clicked file
			String filePath = bottomTextArea.getText();

			// Create a temporary File object from the file path
			File temp = new File(filePath);

			// Create a fileName String to store the name of the file from the path
			String fileName = temp.getName();

			// Push the temporary File object onto the stack
			reOpenFilesStack.push(temp);

			// Create a File object to store the file to be re-opened
			File reOpenFile = null;

			// Iterate through the reOpenFiles
			while (!reOpenFilesStack.isEmpty()) {
				// Get the next file in the stack
				reOpenFile = (File) reOpenFilesStack.peek();
				// If the file name matches the clicked on file name
				if (fileName.equals(reOpenFile.getName())) {
					// Display the contents of the file
					display(reOpenFile, topTextArea, centerTextArea, bottomTextArea);
					break;
				}
				// Pop the file from the stack if the file names don't match
				fileStack.pop();
			}
		});

		// Create the border pane and add the HBox and VBox to it
		BorderPane root = new BorderPane();
		root.setTop(hbox);
		root.setCenter(vbox);

		// Set the scene and show the stage
		Scene scene = new Scene(root, 400, 350);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void display(File selectedFile, TextArea t1, TextArea t2, TextArea t3) {// this function will read from a
																					// file and print the contents into
		// Initialize reader to null // the text Areas
		BufferedReader reader = null;
		// Initialize a string builder to store the contents of the file
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			// Read the contents of the file and store it in the string builder
			reader = new BufferedReader(new FileReader(selectedFile));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// Empty catch block, as it's a;ready handled above
		} catch (NullPointerException n) {
			// If the file is empty, print error message
			System.out.println("file is empty");
		}
		// Convert the contents of the string builder to a string
		String fileContent = sb.toString();

		// Set the file name in the text area
		t1.setText(selectedFile.getName());

		// Check if the file is valid
		if (isValid(selectedFile)) {
			// If the file is valid, search for the <files> tag
			int filesTagStartIndex = fileContent.indexOf("<files>");
			// While the <files> tag is found
			while (filesTagStartIndex != -1) {
				// Find the end index of the <files> tag
				int endIndex = fileContent.indexOf("</files>", filesTagStartIndex);
				String filesTags = null;
				try {
					// Get the content within the <files> tags by using the start and end indices
					filesTags = fileContent.substring(filesTagStartIndex + "<files>".length(), endIndex);
					// If the indices are out of bounds, catch the exception and print an error
					// message
				} catch (StringIndexOutOfBoundsException s) {
					t3.appendText("Invalid file (missing tag)\n");
				}
				// Remove the <file> and </file> tags from the content within the <files> tags
				filesTags = filesTags.replace("<file>", "").replace("</file>", "");
				// Append the content within the <files> tags to the text area
				t3.appendText(filesTags + "\n");
				// Find the start index of the next <files> tag, starting at the end index of
				// the current <files> tag
				filesTagStartIndex = fileContent.indexOf("<files>", endIndex);
			}

			// Find the start index of the <equation> tag
			int equationsTagStartIndex = fileContent.indexOf("<equation>");
			// Initialize a result string to null
			String result = null;
			// While the start index of the <equation> tag is not -1 (meaning it exists in
			// the file)
			while (equationsTagStartIndex != -1) {
				// Find the end index of the <equation> tag, starting at the start index of the
				// <equation> tag
				int endIndex = fileContent.indexOf("</equation>", equationsTagStartIndex);
				try {
					// Get the content within the <equation> tags by using the start and end indices
					String equation = fileContent.substring(equationsTagStartIndex + "<equation>".length(),
							endIndex);
					try {
						// Evaluate the equation and store the result in the result string
						result = evaluate(equation);
						// If the result is "invalid", print the equation and "invalid equation" to the
						// text area
						if (result == "invalid")
							t2.appendText(equation + " ==> " + "invalid equation\n");
						else
							// Else, print the equation and the result to the text area
							t2.appendText(equation + " " + " ==> " + result + "\n");

						// If the equation is invalid, catch the null pointer exception and print an
						// error message
					} catch (NullPointerException e) {
						t2.appendText(equation + " ==> " + "invalid equation\n");
					}

					// If the indices of the tags are unbalanced , catch the exception and print an
					// error message
				} catch (StringIndexOutOfBoundsException s) {
					t2.appendText("Invalid file (missing tag)\n");
					break;
				}
				// Find the start index of the next <equations> tag, starting at the end index
				// of the current <equations> tag
				equationsTagStartIndex = fileContent.indexOf("<equation>", endIndex);
			}

			// If the result string is empty (meaning there are no equations) , print an
			// error message
			if (result == null)
				t2.appendText("This file contains no equations\n");

			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// If the file is not Valid , print an error message
		} else {
			t1.setText("Invalid file (missing tag)\n");
		}
	}

	public boolean isValid(File file) {
		// Check if file exists
		if (!file.exists())
			return false;
		
		// Read file content into a string
		String fileContent = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			fileContent = sb.toString();
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		// Find the start index of the <242> tag
		int startTagIndex = fileContent.indexOf("<242>");
		// Check if start tag exists
		if (startTagIndex == -1)
			return false;
		
		// Find the end index of the <242> tag
		int endTagIndex = fileContent.indexOf("/242", startTagIndex);
		// Check if end tag exists
		if (endTagIndex == -1)
			return false;
		
		// Return true if file is valid
		return true;
	}

	public static String evaluate(String expression) {
		// stack to hold operands and operators
		Stack stack = new Stack();
		// string to hold the postfix expression
		String result = "";
	    // counter for number of open parentheses
		int openParen = 0;
		// counter for number of close parentheses
		int closeParen = 0;
		// loop through the expression
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			// if the character is a digit, add it to the result string
			if (Character.isDigit(c)) {
				result += c;
			}
			// if the character is an operator
			else if (isOperator(c)) {
				// if the operator is in an invalid position
				if (i == 0 || i == expression.length() - 1 || expression.charAt(i - 1) == '('
						|| expression.charAt(i + 1) == ')') {
					// invalid operator placement
					return "invalid";
				}
				// While the stack is not empty and the current character has a lower or equal
				// precedence (Order of Operations) to the top of the stack
				while (!stack.isEmpty() && OrderOfOperations(c) <= OrderOfOperations((char) stack.peek())) {
					// pop the top element and add it to the result string
					result += stack.pop();
				}
				// Push the current character onto the stack
				stack.push(c);
				// If the current character is an open parenthesis, increment the openParen
				// counter and push it onto the stack
			} else if (c == '(') {
				openParen++;
				stack.push(c);
				// If the current character is a close parenthesis, increment the closeParen
				// counter and pop elements from the stack until an open parenthesis is found
			} else if (c == ')') {
				closeParen++;
				// If the stack is empty and the current character is a close parenthesis
				if (stack.isEmpty() && c == ')') {
					// Return "invalid" for a missing operand
					return "invalid";
				}
				while ((char) stack.peek() != '(') {
					result += stack.pop();
				}
				stack.pop();
			}
		}
		// If the number of open and close parentheses do not match
		if (openParen != closeParen) {
			// return "invalid" for mismatched parentheses
			return "invalid"; 
		}
		// While the stack is not empty, pop the remaining elements and add them to the result string
		while (!stack.isEmpty()) {
			result += stack.pop();
		}
		// Iterate through the result string, pushing numbers onto the stack and
		// performing operations on the stack when an operator is found
		for (int i = 0; i < result.length(); i++) {
			char c = result.charAt(i);
			if (Character.isDigit(c)) {
				stack.push(Character.getNumericValue(c));
			} else if (isOperator(c)) {
				int b = (int) stack.pop();
				int a = (int) stack.pop();
				int res = 0;
				if (c == '+') {
					res = a + b;
				} else if (c == '-') {
					res = a - b;
				} else if (c == '*') {
					res = a * b;
				} else if (c == '/') {
					res = a / b;
				}
				stack.push(res);
			}
		}
		// Return the result string and the final result from the stack
		return result + " ==> " + stack.pop();
	}
	
	// this method checks whether the character given is an operator or not
	public static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	
	// this method checks the order of operations
	public static int OrderOfOperations(char c) {
		if (c == '+' || c == '-') {
			return 1;
		} else if (c == '*' || c == '/') {
			return 2;
		} else {
			return 0;
		}
	}

}
