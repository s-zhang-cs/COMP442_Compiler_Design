package buffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

/**
 * Please set Config.buffersize to a small number for testing purpose.
 */
public class BufferTest {
    public static void main(String[] args) throws Exception{
        BufferedReader source = new BufferedReader(new FileReader("resources/buffer/bufferTestInput2"));
        Buffer buffer = new Buffer(source);

        System.out.println("~~~~~test the buffer will not retract if buffer position is at the start~~~~~");
        showBuffers(buffer);
        for(int i = 0; i < Config.bufferSize; i++) {
            buffer.retract();
        }
        showBuffers(buffer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("~~~~~~~~~~~~~~~~test the buffer can retract up to buffer size~~~~~~~~~~~~~~~~");
        showBuffers(buffer);
        for(int i = 0; i < Config.bufferSize; i++) {
            buffer.extend();
        }
        for(int i = 0; i < Config.bufferSize; i++) {
            buffer.retract();
        }
        showBuffers(buffer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("~~~~~~~~~~~~~~~~test the buffer can extend back after retract~~~~~~~~~~~~~~~~");
        showBuffers(buffer);
        for(int i = 0; i < Config.bufferSize; i++) {
            buffer.extend();
        }
        showBuffers(buffer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~test the save/load functions~~~~~~~~~~~~~~~~~~~~~~~~");
        Random rand = new Random();
        for(int i = 0; i < rand.nextInt(26); i++) {
            buffer.extend();
        }
        //save
        int[] save = buffer.save();
        showBuffers(buffer);
        //do modifications
        for(int i = 0; i < rand.nextInt(Config.bufferSize); i++) {
            buffer.extend();
        }
        //load
        buffer.load(save);
        showBuffers(buffer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~test the copylexeme function~~~~~~~~~~~~~~~~~~~~~~~~");
        for(int i = 0; i < rand.nextInt(26); i++) {
            buffer.extend();
        }
        System.out.println(buffer.copyLexeme());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    private static void showBuffers(Buffer buffer) {
        buffer.showBuffer1();
        buffer.showBuffer2();
    }
}
