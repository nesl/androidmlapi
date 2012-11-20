package edu.ucla.nesl.mca.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class ClassifierBuilder {
    public static Classifier BuildFromFile (String path) throws IOException {
        // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
        FileInputStream stream = new FileInputStream(new File(path));
        String jsonString;
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            jsonString = Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
        
        return Build(jsonString);
      }
    
    public static Classifier Build(String jsonString) throws IOException {
        String modelType = Classifier.getJSONModelType(jsonString);
        Classifier cl;
        if (modelType.equals("TREE"))
            cl = new DecisionTree();
        else
            throw new IOException("[ClassifierBuilder] cannot recognize the model type.");
        
        cl.parseJSON(jsonString);
        
        return cl;
    }

    /**
     * Test ClassifierBuilder
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            Classifier cl = ClassifierBuilder.BuildFromFile("testFiles\\JSON-TransportationMode.txt");
            cl.evaluate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
