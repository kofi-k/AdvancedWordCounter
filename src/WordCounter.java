import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;


public class WordCounter extends JFrame implements ActionListener{

    JTextArea textArea;
    JPanel jPanel;
    JButton button;
    public WordCounter() {
        jPanel = new JPanel();
        textArea = new JTextArea(20,20);
        textArea.setEditable(false);
        textArea.setFont(new Font("Sans", Font.PLAIN, 16));
        textArea.setLineWrap(true);

        button = new JButton ("Start Count");
        button.setBounds(10,10,365,290);
        add(jPanel);
        jPanel.add(button);
        jPanel.add(textArea);
        button.addActionListener(this);

    }
    public static void main(String[] args)
    {
        //creating frame to display GUI content
        JFrame wordCounterWindow = new WordCounter();
        wordCounterWindow.setTitle("Avanced Word Counter");
        wordCounterWindow.setSize(500, 500);
        wordCounterWindow.setLocationRelativeTo(null);
        wordCounterWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wordCounterWindow.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        //onButton click, do count words from file
        if (e.getSource()==button)
        {
            try {
                CountWords();//word counter function call
            } catch (IOException e1) {

                e1.printStackTrace();
            }
        }
    }

    public void CountWords() throws IOException {
        FileReader fileReader = new FileReader ("C:\\files\\testDoc.txt");
        FileReader punctFile = null;

        //bufferedReader to read content of the files
        BufferedReader punctRead = GenerateBuffer("C:\\files\\punctuationList.txt");
        String puncts;//to be used to hold lines of punctuation from punctuation file
        int NumOfPunctChar =0, countStopWords=0, originalWords=0;
        char [] punctsArray = null;//array to hold each punctuation character
        List<String> listOfStrings = new ArrayList<>();//list of all words to be read from file
        List<String> EnglishStopWords = new ArrayList<>();//list of english stop words
        String s = null; char ch = 0;

        String[] array; //create array from lists of words from file
        String[] arrayStopWords = null;//array from english stop words list

        //converting strings of punctuation (captured by the 'puncts') into a char array
        //right after reading from file
        while((puncts=punctRead.readLine())!=null) {
            punctsArray = puncts.toCharArray();
        }
        //assign or putting stop words into array
        //see implementation of method createArrayFromFile outside this method
        arrayStopWords = createArrayFromFile(new FileReader( "C:\\files\\stopWords.txt")
        );


        String string = null; char c;
        while (fileReader.ready()) {
            c = (char)fileReader.read();//read words in file character by character

            //if fetched character is a space then it means we have a word
            //then we can clear the content of string to capture another word, character by character
            if (c == ' '||Character.isSpace(c)) {
                listOfStrings.add(string.toString().toString().toLowerCase());//added word is converted to lower-case
                string = new String();

            }else {
                //else character keeps adding up to form a word
                string += c;
            }
            //check if character is a punctuation and count as punctuation
            //we don't remove them immediately here since some characters are between words
            for (char value : punctsArray) {
                if (c == value) {
                    NumOfPunctChar++;
                    listOfStrings.add(string.toString());
                    string = new String();
                }
            }//end of for loop
        }//end of while loop

        //clearing blanks from the list
        listOfStrings.removeIf(String::isBlank);
        array = listOfStrings.toArray(new String[0]);//storing the list of words in an array

        //each element in the arrary is picked and checked character by character
        //by comparing it to all punctuation characters in the punctuationArray
        for(int a=0;a<array.length;a++) {//looping in the main array,
            String word=array[a];	//each word is moved to a string variable
            for(int l=0;l<word.length();l++) {//looping in the length of the word, which will vary
                for (char value : punctsArray) {//looping in the punctuation array
                    if (word.charAt(l) == value) {//check each character of the word
                        StringBuilder sb = new StringBuilder(word);//stringbuilder class is used to remove
                        sb.deleteCharAt(l);                        //character at a certain position in the
                        array[a] = sb.toString();                       //word if it is a punctuation
                    }

                    //remove all stand alone single numbers from the array eg, 1, 6
                    if (array[a].length() == 1) {
                        if (Character.isDigit(word.charAt(0))) {
                            array = removeElement(array, a);
                        }
                    }

                }//end of punctuation loop
            }//end of word loop

            //same approach to remove to stops from the list
            //this time check is done word by word not characters
            for (String arrayStopWord : arrayStopWords) {
                if (array[a].equals(arrayStopWord)) {
                    //see implementation of removeElement method outside this method
                    array = removeElement(array, a);
                    countStopWords++;//count number of english stop words removed
                }
            }//end of stop words loop
        }//end of looping in the array

        //using the Array class to check our array and remove all empty or null elements
        //in our array after the removal of punctuation and english stop words
        array =Arrays.stream(array)
                .filter(value ->value != null && value.length() > 0)
                .toArray(String[]::new);
        //display data on textArea
        textArea.append("Original words: "+(array.length+countStopWords)+"\n");
        textArea.append("Unique words: "+(array.length)+"\n");
        textArea.append("Punctuations removed: "+NumOfPunctChar+"\n");
        textArea.append("English stop words removed: "+countStopWords+"\n");

        //to count the occurance of words, we use the treemap to loop through our array
        Map<String, Integer> wordCounts = new TreeMap<>();
        //word is assigned to a string
        for (String next : array) {
            if (!wordCounts.containsKey(next)) {
                wordCounts.put(next, 1);//if a word is not found again in the array, assign value as 1
            } else { //else, add 1 to its count on every encounter
                wordCounts.put(next, wordCounts.get(next) + 1);
            }
        }
        //treemap automatically sorts words into alphabetical order so we need to create
        //new array of our words from that, hence we create separate lists for the words
        //and its integer count as well
        ArrayList<String> wordList = new ArrayList<>(wordCounts.keySet());
        String []wordsArray = wordList.toArray(new String[0]);
        ArrayList<Integer> countList = new ArrayList<>(wordCounts.values());
        Integer []wordsCount = countList.toArray(new Integer[0]);
        //temporary count and word holder to be used in sorting words
        int value;		String vWord=null;

        //now we can sort array in descending order by using the generated integer array of the
        //counted words
        for(int i=0; i<wordsCount.length;i++) {//looping in the main array of counted words
            for(int j=i+1;j<wordsCount.length;j++) {//looping a number through all other numbers in the same array
                if(wordsCount[i] < wordsCount[j]) {//if first or previous number is less than next number (notice j is 1, ahead i)
                    value = wordsCount[i]; //assign the lesser number to our temporary variable
                    vWord = wordsArray[i]; //assign its equivalent word also
                    wordsCount[i]=wordsCount[j]; //we move the higher number to the position of the lesser number
                    wordsArray[i]=wordsArray[j];//move its equivalent word also
                    wordsCount[j]=value;//now assign lesser number back to the position of the higher number
                    wordsArray[j]=vWord; //likewise for its word
                }
            }
        }//end of loop

        //print sorted array in textArea
        textArea.append("\nFrequency \tword\n");
        for(int i=0; i<wordsCount.length;i++) {
            textArea.append("   " +wordsCount[i] + "\t" + wordsArray[i]+"\n");
        }
        fileReader.close();
    }

    //same logic used reading words from file, character by character as commented in CountWords method
    public static String[] createArrayFromFile(FileReader fileReader) throws IOException {
        List<String> list = new ArrayList<>();
        StringBuilder string = new StringBuilder(new String());
        while (fileReader.ready()) {
            char c = (char) fileReader.read();
            if (c == ' '||Character.isSpace(c) ) {
                list.add(string.toString().toLowerCase());
                string = new StringBuilder();
            }
            else {
                string.append(c);
            }
        }
        if (string.length() > 0) {
            list.add(string.toString());
        }
        //read file containing punctuation
        list.removeIf(String::isBlank);
        String[] fileArray = list.toArray(new String[0]);
        fileReader.close();
        return fileArray;
    }

    //method takes an array and an index (int) to remove the element at that index
    public static String[] removeElement(String[] array, int index)
    {
        //checks for an empty array or element in the array
        if (array == null || index < 0
                || index >= array.length) {
            return array;
        }
        // Create another array of a size one less than the original
        String[] newArray = new String[array.length - 1];
        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < array.length; i++) {

            //skips if index is reached
            if (i == index) {
                continue;
            }
            // if the index is reached move elements to new arrayy
            newArray[k++] = array[i];
        }
        // return the new array
        return newArray;
    }

    //takes file reader and file directory to generate bufferedreader
    public static BufferedReader GenerateBuffer(String fileDir) throws IOException {
        FileReader fileReader = new FileReader(fileDir);
        return new BufferedReader(fileReader);
    }

}
