package buffer;

import java.io.BufferedReader;
import java.io.IOException;

public class Buffer {

    private BufferedReader source;

    private int currentPtr;
    private int forwardPtr;

    private char[] buffer1;
    private char[] buffer2;

    private boolean onBuffer1;
    private boolean bothBuffersLoaded;
    private boolean otherBufferIsNewer;

    public Buffer(BufferedReader source) throws IOException {
        this.source = source;
        currentPtr = 0;
        forwardPtr = 0;
        buffer1 = new char[Config.bufferSize];
        buffer2 = new char[Config.bufferSize];
        onBuffer1 = true;
        bothBuffersLoaded = false;
        otherBufferIsNewer = false;
        loadBuffer(buffer1);
    }

    public int getForwardPtr() {
        return forwardPtr;
    }

    public int getCurrentPtr() {
        return currentPtr;
    }

    public void synchPtrBasedOnForwardPtr() {
        currentPtr = forwardPtr;
    }

    public void synchPtrBasedOnCurrentPtr() {
        forwardPtr = currentPtr;
    }

    public int[] save() {
        int[] save = new int[3];
        save[0] = currentPtr;
        save[1] = forwardPtr;
        save[2] = onBuffer1 ? 1 : 0;
        return save;
    }

    public void load(int[] save) {
        currentPtr = save[0];
        forwardPtr = save[1];
        if((save[2] == 1) != onBuffer1){
            otherBufferIsNewer = true;
        }
        onBuffer1 = (save[2] == 1) ? true : false;
    }

    public char peek() {
        if(onBuffer1) {
            return buffer1[forwardPtr];
        }
        return buffer2[forwardPtr];
    }

    public void showBuffer1() {
        System.out.println("[Buffer1]");
        for(char c : buffer1) {
            if((int)c >= 32 && (int)c <= 126){
                System.out.print("\'" + c + "\'");
            }
            else {
                System.out.print((int) c);
            }
            System.out.print(' ');
        }
        if(onBuffer1) {
            System.out.print("| forwardPtr: " + forwardPtr);
            if(!otherBufferIsNewer) {
                System.out.println(" | contains newer data");
            }
            else {
                System.out.println();
            }
        }
        else {
            System.out.println();
        }
    }

    public void showBuffer2() {
        System.out.println("[Buffer2]");
        for(char c : buffer2) {
            if((int)c >= 32 && (int)c <= 126){
                System.out.print("\'" + c + "\'");
            }
            else {
                System.out.print((int) c);
            }
            System.out.print(' ');
        }
        if(!onBuffer1) {
            System.out.print("| forwardPtr: " + forwardPtr);
            if(!otherBufferIsNewer) {
                System.out.println(" | contains newer data");
            }
            else {
                System.out.println();
            }
        }
        else {
            System.out.println();
        }
    }

    //assuming no forward looking goes past Config.bufferSize
    public String copyLexeme() {
        int begin = currentPtr, end = forwardPtr;
        StringBuilder sb = new StringBuilder();
        if(begin == end) {}
        else if(begin < end) {
            if(onBuffer1) {
                for(int i = begin; i < end; i++) {
                    sb.append(buffer1[i]);
                }
            }
            else {
                for(int i = begin; i < end; i++) {
                    sb.append(buffer2[i]);
                }
            }
        }
        else {
            if(onBuffer1) {
                for(int i = begin; i < Config.bufferSize; i++) {
                    sb.append(buffer2[i]);
                }
                for(int i = 0; i < end; i++) {
                    sb.append(buffer1[i]);
                }
            }
            else {
                for(int i = begin; i < Config.bufferSize; i++) {
                    sb.append(buffer1[i]);
                }
                for(int i = 0; i < end; i++) {
                    sb.append(buffer2[i]);
                }
            }
        }
        return sb.toString();
    }

    public void retract() {
        forwardPtr--;
        if(forwardPtr < 0) {
            //flag to disable retract operation if only 1 buffer has been initialized
            if(bothBuffersLoaded) {
                otherBufferIsNewer = true;
                forwardPtr = ((forwardPtr % Config.bufferSize) + Config.bufferSize) % Config.bufferSize;
                onBuffer1 = !onBuffer1;
            }
            else {
                forwardPtr = 0;
            }
        }
    }

    public void extend() throws IOException{
        forwardPtr++;
        if(forwardPtr >= Config.bufferSize) {
            if(!otherBufferIsNewer) {
                reloadBuffer();
            }
            else {
                forwardPtr = 0;
                onBuffer1 = !onBuffer1;
                otherBufferIsNewer = false;
            }
        }
    }

    private void loadBuffer(char[] buffer) throws IOException {
        for(int i = 0; i < Config.bufferSize; i++) {
            int read = source.read();
            if(read == -1) {
                buffer[i] = Config.sentinel;
                break;
            }
            buffer[i] = (char)read;
        }
    }

    private void reloadBuffer() throws IOException {
        if(onBuffer1) {
            loadBuffer(buffer2);
        }
        else {
            loadBuffer(buffer1);
        }
        forwardPtr = 0;
        onBuffer1 = !onBuffer1;
        bothBuffersLoaded = true;
    }
}




