package calculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Class1  extends JFrame implements ActionListener{
	
	private JTextField textField;
    private StringBuilder currentExpression = new StringBuilder();
    private double result = 0;

    public Class1() {
        setTitle("Visual Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setEditable(false);
        add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 5));
        String[] buttonLabels = {"7", "8", "9", "DEL","AC",
				                "4", "5", "6", "*","/",
				                "1", "2", "3", "+","-",
				                "0", ".", "(", ")", "="}; 

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("=")) {
            result = evaluateExpression(currentExpression.toString());
            textField.setText(Double.toString(result));
            currentExpression.setLength(0);
            currentExpression.append(result);
        } else if (command.equals("AC")) {
            currentExpression.setLength(0);
            textField.setText("");
        } else if (command.equals("DEL")) {
            if (currentExpression.length() > 0) {
                currentExpression.deleteCharAt(currentExpression.length() - 1);
                textField.setText(currentExpression.toString());
            }
        } else {
            currentExpression.append(command);
            textField.setText(currentExpression.toString());
        }
    }

    private double evaluateExpression(String expression) {
        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (Character.isWhitespace(ch))
                        nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length())
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+'))
                            x += parseTerm();
                        else if (eat('-'))
                            x -= parseTerm();
                        else
                            return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*'))
                            x *= parseFactor();
                        else if (eat('/'))
                            x /= parseFactor();
                        else
                            return x;
                    }
                }

                double parseFactor() {
                    if (eat('+'))
                        return parseFactor();
                    if (eat('-'))
                        return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.')
                            nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    return x;
                }
            }.parse();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid expression");
            return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Class1();
            }
        });
    }
}