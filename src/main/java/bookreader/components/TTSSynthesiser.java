package bookreader.components;

import bookreader.utils.TextUtils;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.*;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class TTSSynthesiser {

    /**
     * The sound that is currently playing.
     */
    private MediaPlayer currentSound = null;

    /**
     * An array containing all word media files in order.
     */
    private MediaPlayer[] wordSounds = null;

    /**
     * Currently selected language. Should be a language code compatible
     * with tesseract, usually only 'bg' and 'en'.
     */
    private String language = null;

    // Dependencies
    private final TextHighlighter textHighlighter;
    private final TextUtils textUtils;

    @Autowired
    public TTSSynthesiser(
            TextHighlighter textHighlighter,
            TextUtils textUtils
    ) {
        this.textHighlighter = textHighlighter;
        this.textUtils = textUtils;
    }

    public void testGoogle() throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText("Hello, World!").build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("en-US")
                            .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
            }
        }
    }

    public void testFreeTTS() throws EngineException, AudioException, InterruptedException {
        System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us"
                        + ".cmu_us_kal.KevinVoiceDirectory");

        // Register Engine
        Central.registerEngineCentral(
                "com.sun.speech.freetts"
                        + ".jsapi.FreeTTSEngineCentral");

        Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc((Locale.US)));
        synthesizer.allocate();
        synthesizer.addSpeakableListener(new SpeakableListener() {
            @Override
            public void markerReached(SpeakableEvent speakableEvent) {
                System.out.println("Marker Reached");
            }

            @Override
            public void speakableCancelled(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Cancelled");
            }

            @Override
            public void speakableEnded(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Ended");
            }

            @Override
            public void speakablePaused(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Paused");
            }

            @Override
            public void speakableResumed(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Resumed");
            }

            @Override
            public void speakableStarted(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Started");
            }

            @Override
            public void topOfQueue(SpeakableEvent speakableEvent) {
                System.out.println("Top of Queue");
            }

            @Override
            public void wordStarted(SpeakableEvent speakableEvent) {
                System.out.println("Word Started");
            }
        });
        synthesizer.speakPlainText(
                "Hello, this is", null);
        synthesizer.speakPlainText(
                "a test message.", null);
        synthesizer.speakPlainText(
                "How are you doing?", null);

        synthesizer.resume();
        synthesizer.waitEngineState(
                Synthesizer.QUEUE_EMPTY);
        // Deallocate the Synthesizer.
        synthesizer.deallocate();
    }

    public void testFreeTTS(String msg) throws EngineException, AudioException, InterruptedException {
        System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us"
                        + ".cmu_us_kal.KevinVoiceDirectory");

        // Register Engine
        Central.registerEngineCentral(
                "com.sun.speech.freetts"
                        + ".jsapi.FreeTTSEngineCentral");

        Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc((Locale.US)));
        synthesizer.allocate();
        synthesizer.resume();
        synthesizer.addSpeakableListener(new SpeakableListener() {
            @Override
            public void markerReached(SpeakableEvent speakableEvent) {
                System.out.println("Marker Reached");
            }

            @Override
            public void speakableCancelled(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Cancelled");
            }

            @Override
            public void speakableEnded(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Ended");
            }

            @Override
            public void speakablePaused(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Paused");
            }

            @Override
            public void speakableResumed(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Resumed");
            }

            @Override
            public void speakableStarted(SpeakableEvent speakableEvent) {
                System.out.println("Speakable Started");
            }

            @Override
            public void topOfQueue(SpeakableEvent speakableEvent) {
                System.out.println("Top of Queue");
            }

            @Override
            public void wordStarted(SpeakableEvent speakableEvent) {
                System.out.println("Word Started");
            }
        });
        synthesizer.speakPlainText(msg, null);

        synthesizer.waitEngineState(
                Synthesizer.QUEUE_EMPTY);
        // Deallocate the Synthesizer.
        synthesizer.deallocate();
    }

    public void testPythonGTTS() {
        System.out.println("Start");

        try {
            //ProcessBuilder pb = new ProcessBuilder("gtts-cli", "'hello'", "--output", "hello" + i + ".mp3");
            ProcessBuilder pb = new ProcessBuilder("gtts-cli", "'hello'");
            //pb.directory(new File("data/tts/"));
            var process = pb.start();
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

    public void testPythonReadText(String text) {
        ProcessBuilder pb = new ProcessBuilder("gtts-cli", text,
                "--output", "text.mp3",
                "--lang", "bg");
        pb.directory(new File("data/tts/"));
        Process process = null;
        try {
            //System.out.println("Frog1");
            process = pb.start();
            process.waitFor();
            //System.out.println("Frog2");
            var mp = new MediaPlayer(new Media(new File("data/tts/text.mp3").toURI().toString()));
            //System.out.println(mp.getStatus());
            //System.out.println("Frog3");
            mp.play();
            //System.out.println("Frog4");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the language for the synthesiser to use forward. Must be
     * set before being able to load text.
     * @param lang The language code for the synthesiser. Should be
     *             either 'bg' or 'en', or any code compatible with
     *             tesseract.
     */
    public void setLanguage(String lang) {
        this.language = lang;
    }

    /**
     * Checks whether the synthesiser is in a state where it can load text.
     *
     * Currently, the only condition is for the language to be set.
     * @return True if it can, false otherwise.
     */
    public boolean canBeLoaded() {
        return this.language != null;
    }

    /**
     * Loads the sounds of the words and sentences of a text in advance to
     * reduce load time while reading.
     * @param words List of the words in the text.
     * @throws IllegalStateException If the synthesiser is not in a state to be
     * currently loaded.
     * @see TTSSynthesiser#canBeLoaded()
     */
    public void loadSounds(List<String> words) throws IllegalStateException{
        if (!canBeLoaded()) {
            throw new IllegalStateException("The synthesiser is not in a state to be loaded!");
        }

        this.wordSounds = new MediaPlayer[words.size()];

        new Thread(() -> {
            for (int i = 0; i < words.size(); i++) {
                loadIndividual(i, words.get(i));
            }
        }).start();
    }

    /**
     * Loads a word at a given index if it wasn't already loaded.
     * @param index Index of the word in the words list.
     * @param text The text to load.
     */
    private void loadIndividual(int index, String text) {
        if (wordSounds[index] != null) return;
        ProcessBuilder pb = new ProcessBuilder("gtts-cli",
                "--output", "word" + index + ".mp3",
                "--lang", this.language,
                text);
        pb.directory(new File("data/tts/"));
        try {
            Process process = pb.start();
            boolean finished = process.waitFor(1000, TimeUnit.MILLISECONDS);

            if (finished) {
                wordSounds[index] = new MediaPlayer(new Media(new File("data/tts/word" + index + ".mp3").toURI().toString()));
                wordSounds[index].setOnReady(() -> {
                    Duration subtractDuration = textUtils.getSubtractDuration(text);
                    wordSounds[index].setStopTime(wordSounds[index].getTotalDuration().subtract(subtractDuration));
                });

            }
            //wordSounds[index].getTotalDuration().subtract(Duration.millis(100));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the currently playing sound.
     * @return A mediaplayer object if there is a sound currently playing,
     * false otherwise.
     */
    public MediaPlayer getCurrentSound() {
        return this.currentSound;
    }

    /**
     * Starts reading the text from the beginning.
     */
    public void readFromStart() {
        stopCurrentRead();
        textHighlighter.clearSelection();
        if (this.wordSounds == null) {
            throw new NullPointerException("There were no loaded sounds! wordSounds was null.");
        }
        chainReadWords(0, wordSounds.length-1, true);
    }

    public void readRange(int selectionStart, int selectionEnd) {
        stopCurrentRead();
        if (this.wordSounds == null) {
            throw new NullPointerException("There were no loaded sounds! wordSounds was null.");
        }
        chainReadWords(selectionStart, selectionEnd, false);
    }

    private void chainReadWords(int startingIndex, int lastIndex, boolean shiftSelection) {
        //System.out.println("Read word: " + index);
        if (wordSounds[startingIndex] == null) {
            loadIndividual(startingIndex, textHighlighter.getWordAt(startingIndex));
        }
        //System.out.println("Out of loop: " + index);
        if (shiftSelection) textHighlighter.selectNextWord();
        this.currentSound = wordSounds[startingIndex];
        this.currentSound.setOnEndOfMedia(() -> {
            //System.out.println("Word " + index + " End: " + System.currentTimeMillis());
            if (startingIndex < wordSounds.length - 1 && startingIndex < lastIndex) {
                chainReadWords(startingIndex+1, lastIndex, shiftSelection);
            } else {
                this.currentSound = null;
            }
        });
        this.currentSound.seek(Duration.ZERO);
        this.currentSound.play();
        //System.out.println("Word " + index + " Start: " + System.currentTimeMillis());
    }

    public void readSpecificWord(int index) {
        stopCurrentRead();
        textHighlighter.selectSpecificWord(index);
        String word = textHighlighter.getCurrentSelection();
        loadIndividual(index, word);
        this.currentSound = wordSounds[index];
        this.currentSound.setOnEndOfMedia(() -> {
            this.currentSound = null;
        });
        this.currentSound.seek(Duration.ZERO);
        this.currentSound.play();
    }

    /**
     * Stops whatever is currently being read, if any.
     */
    public void stopCurrentRead() {
        if (this.currentSound == null) return;
        this.currentSound.stop();
        this.currentSound = null;
    }
}
