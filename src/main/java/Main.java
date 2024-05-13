import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final String LETTERS = "a,b,c";
    private static final int TEXT_LENGTH = 10;
    private final static int NUM_TEXTS = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(NUM_TEXTS);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(NUM_TEXTS);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(NUM_TEXTS);

    public static void main(String[] args) {
        // Создание и запуск потока для генерации текстов
        Thread textGeneratorThread = new Thread(Main::generateText);
        textGeneratorThread.start();

        // Создание и запуск потока для анализа всех символов
        Thread analysisThread = new Thread(Main::analyzeAllChars);
        analysisThread.start();

        // Ожидание завершения всех потоков
        try {
            textGeneratorThread.join(); // Ожидание завершения потока генерации текстов
            analysisThread.join(); // Ожидание завершения потока анализа всех символов
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вывод результатов
        System.out.println("Максимальное количество символов 'a' в тексте: " + queueA.size());
        System.out.println("Максимальное количество символов 'b' в тексте: " + queueB.size());
        System.out.println("Максимальное количество символов 'c' в тексте: " + queueC.size());
    }

    private static void generateText() {
        Random random = new Random();
        for (int i = 0; i < TEXT_LENGTH; i++) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < TEXT_LENGTH; j++) {
                text.append(LETTERS.charAt(random.nextInt(LETTERS.length())));

                try {
                    queueA.put(text.toString());
                    queueB.put(text.toString());
                    queueC.put(text.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void analyzeAllChars() {
        analyzeChar('a', queueA);
        analyzeChar('b', queueB);
        analyzeChar('c', queueC);
    }

    private static void analyzeChar(char c, BlockingQueue<String> queue) {
        int maxCount = 0;

        try {
            while (true) { //
                String text = queue.poll();
                if (text == null) break;
                int count = 0;
                // Подсчет количества символов char в текущем тексте из очереди
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) == c) {
                        count++;
                    }
                }

                maxCount = Math.max(maxCount, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
