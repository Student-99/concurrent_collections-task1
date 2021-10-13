import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static Random random = new Random();
    static ConcurrentLinkedQueue<User> userATS = new ConcurrentLinkedQueue<>();
    final static int countUser = random.nextInt(10) + 50;
    final static int countEmployee = 10;
    final static int amountOfTimeToProcessCall = 3000;
    final static int frequencyOfCallGeneration = 1000;


    public static void main(String[] args) {
        System.out.println(String.format("Будет создано %s пользовательских звонков\n", countUser));
        ExecutorService poolEmployees = Executors.newFixedThreadPool(countEmployee);

        Runnable runnableCalls = () -> generateCalls();
        Thread calls = new Thread(runnableCalls);
        calls.start();

        Runnable runnableEmployee = () -> {
            while (calls.isAlive() | !userATS.isEmpty()) {
                poolEmployees.submit(() -> {
                    userConsultation(userATS);
                });
            }
            poolEmployees.shutdown();
        };
        Thread threadEmployee = new Thread(runnableEmployee);
        threadEmployee.start();
        Runnable mainRunnable = () -> {
            while (!poolEmployees.isTerminated()) {
            }
            System.out.println("Все клиенты были обработаны операторами");
        };
        new Thread(mainRunnable).start();
    }

    public static void userConsultation(ConcurrentLinkedQueue<User> concurrentLinkedQueue) {
        User user = concurrentLinkedQueue.poll();
        if (user == null) {
            return;
        }
        System.out.println(String.format("Оператор %s начал консультировать клиента %s",
                Thread.currentThread().getName(), user.getName()));
        try {
            Thread.sleep(amountOfTimeToProcessCall);
        } catch (InterruptedException e) {
        }
        System.out.println(String.format("Оператор %s закончил консультировать клиента %s",
                Thread.currentThread().getName(), user.getName()));
    }

    public static void generateCalls() {
        for (int i = 0; i < countUser; i++) {
            userATS.offer(new User("Пользователь " + i));
            try {
                Thread.sleep(frequencyOfCallGeneration);
            } catch (InterruptedException e) {
            }
        }
    }
}
