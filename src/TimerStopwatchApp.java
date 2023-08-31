import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimerStopwatchApp {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel timerPanel;
    private JPanel stopwatchPanel;
    private JLabel timerLabel;
    private JLabel stopwatchLabel;
    private Timer timer;
    private long startTime;
    private boolean stopwatchRunning;
    private ArrayList<String> lapTimes;
    private JTextArea lapTextArea;
    private boolean alarmShown = false;

    public TimerStopwatchApp() {
        frame = new JFrame("Timer and Stopwatch App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500); // Increased height for better lap display

        tabbedPane = new JTabbedPane();

        timerPanel = new JPanel();
        timerPanel.setLayout(new FlowLayout());
        timerLabel = new JLabel("Timer: 0");
        JButton controlTimerButton = new JButton("Start/Reset Timer");

        controlTimerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    timerLabel.setText("Timer: 0");
                } else {
                    startTimer();
                }
            }
        });

        timerPanel.add(timerLabel);
        timerPanel.add(controlTimerButton);

        stopwatchPanel = new JPanel();
        stopwatchPanel.setLayout(new BorderLayout());
        stopwatchLabel = new JLabel("Stopwatch: 00:00:00");
        JButton startStopwatchButton = new JButton("Start/Stop Stopwatch");
        JButton lapButton = new JButton("Lap");
        JButton resetLapsButton = new JButton("Reset Laps");

        lapTextArea = new JTextArea(15, 30); // Set rows and columns
        lapTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(lapTextArea);

        JPanel stopwatchButtonPanel = new JPanel();
        stopwatchButtonPanel.setLayout(new FlowLayout());
        stopwatchButtonPanel.add(startStopwatchButton);
        stopwatchButtonPanel.add(lapButton);
        stopwatchButtonPanel.add(resetLapsButton);

        startStopwatchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!stopwatchRunning) {
                    startStopwatch();
                } else {
                    stopStopwatch();
                }
            }
        });

        lapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (stopwatchRunning) {
                    recordLap();
                }
            }
        });

        resetLapsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetLaps();
            }
        });

        stopwatchPanel.add(stopwatchLabel, BorderLayout.NORTH);
        stopwatchPanel.add(stopwatchButtonPanel, BorderLayout.CENTER);
        stopwatchPanel.add(scrollPane, BorderLayout.SOUTH);

        tabbedPane.addTab("Timer", timerPanel);
        tabbedPane.addTab("Stopwatch", stopwatchPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        lapTimes = new ArrayList<>();
    }

    private void startTimer() {
        int seconds = getTimerDuration();
        timerLabel.setText("Timer: " + seconds);
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            int remainingSeconds = seconds;

            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    timerLabel.setText("Timer: " + remainingSeconds);
                    remainingSeconds--;
                } else {
                    timerLabel.setText("Timer: 0");
                    timer.stop();
                    playAlarm();
                }
            }
        });
        timer.start();
    }

    private void playAlarm() {
        if (!alarmShown) {
            alarmShown = true;
            JOptionPane.showMessageDialog(frame, "Time's up!", "Alarm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void startStopwatch() {
        startTime = System.currentTimeMillis();
        stopwatchRunning = true;
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - startTime) / 1000;
                String formattedTime = formatTime(elapsedTime);
                stopwatchLabel.setText("Stopwatch: " + formattedTime);
            }
        });
        timer.start();
    }

    private void stopStopwatch() {
        timer.stop();
        stopwatchRunning = false;
    }

    private void recordLap() {
        if (lapTimes.size() == 0 || stopwatchRunning) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000;
            String formattedTime = formatTime(elapsedTime);
            lapTimes.add(formattedTime);
            updateLapDisplay();
        }
    }

    private void resetLaps() {
        lapTimes.clear();
        updateLapDisplay();
    }

    private void updateLapDisplay() {
        StringBuilder lapText = new StringBuilder();
        for (int i = 0; i < lapTimes.size(); i++) {
            lapText.append("Lap ").append(i + 1).append(": ").append(lapTimes.get(i)).append("\n");
        }
        lapTextArea.setText(lapText.toString());
        lapTextArea.setCaretPosition(lapTextArea.getDocument().getLength());
    }

    private int getTimerDuration() {
        String input = JOptionPane.showInputDialog(frame, "Enter the timer duration (in seconds):");
        int seconds = Integer.parseInt(input);
        return seconds;
    }

    private String formatTime(long elapsedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(elapsedTime * 1000));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TimerStopwatchApp();
            }
        });
    }
}
