import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    // Параметры генератора текстов
    private static final String LETTERS = "abc"; // Доступные символы для генерации текстов
    private static final int TEXT_LENGTH = 100000; // Длина каждого текста
    private static final int NUM_TEXTS = 10000; // Количество генерируемых текстов
d
    // Потокобезопасные блокирующие очереди для каждого символа
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100); // Очередь для символа 'a'
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100); // Очередь для символа 'b'
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100); // Очередь для символа 'c'

    public static voidd main(String[] args) {
        // Создание и запуск потока для генерации текстов
        Thread textGeneratorThread = new Thread(Main::generateTexts);
        textGeneratorThread.start();

        // Создание и запуск по потока для каждого из трёх символов
        Thread aCounterThread = new Thread(() -> countChar('a', queueA)); // Поток для анализа символа 'a'
        Thread bCounterThread = new Thread(() -> countChar('b', queueB)); // Поток для анализа символа 'b'
        Thread cCounterThread = new Thread(() -> countChar('c', queueC)); // Поток для анализа символа 'c'

        aCounterThread.start(); // Запуск потока для символа 'a'
        bCounterThread.start(); // Запуск потока для символа 'b'
        cCounterThread.start(); // Запуск потока для символа 'c'

        // Ожидание завершения всех потоков
        try {
            textGeneratorThread.join(); // Ожидание завершения потока генерации текстов
            aCounterThread.join(); // Ожидание завершения потока анализа символа 'a'
            bCounterThread.join(); // Ожидание завершения потока анализа символа 'b'
            cCounterThread.join(); // Ожидание завершения потока анализа символа 'c'
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вывод результатов
        System.out.println("Максимальное количество символов 'a' в тексте: " + queueA.size());
        System.out.println("Максимальное количество символов 'b' в тексте: " + queueB.size());
        System.out.println("Максимальное количество символов 'c' в тексте: " + queueC.size());
    }

    // Метод для генерации текстов и их добавления в очереди
    private static void generateTexts() {
        Random random = new Random();
        for (int i = 0; i < NUM_TEXTS; i++) { // Перебор по количеству текстов
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < TEXT_LENGTH; j++) { // Генерация каждого текста
                text.append(LETTERS.charAt(random.nextInt(LETTERS.length()))); // Добавление случайного символа в текст
            }
            try {
                // Добавление текста в соответствующую очередь
                switch (text.charAt(0)) { // Определение, в какую очередь добавлять текст
                    case 'a':
                        queueA.put(text.toString()); // Добавление текста в очередь 'a'
                        break;
                    case 'b':
                        queueB.put(text.toString()); // Добавление текста в очередь 'b'
                        break;
                    case 'c':
                        queueC.put(text.toString()); // Добавление текста в очередь 'c'
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для подсчета количества символов char в очереди
    private static void countChar(char c, BlockingQueue<String> queue) {
        int maxCount = 0; // Максимальное количество символов char
        try {
            while (!queue.isEmpty()) { // Пока очередь не пуста
                int count = 0; // Счетчик символов char в текущей порции текстов из очереди
                // Подсчет количества символов char в текущей порции текстов из очереди
                while (!queue.isEmpty()) {
                    String text = queue.take(); // Извлечение текста из очереди
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) == c) { // Если символ из текста равен char
                            count++; // Увеличение счетчика
                        }
                    }
                }
                // Обновление максимального значения
                maxCount = Math.max(maxCount, count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Добавление максимального значения в очередь
        try {
            queue.put(String.valueOf(maxCount)); // Добавление значения в очередь
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
