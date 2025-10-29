package client;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = "-t", description = "Type of request")
    private String type;

    @Parameter(names =  "-k", description = "Index of the cell")
    private String key;

    @Parameter(names = "-v", description = "Message to store")
    private String value;

    @Parameter(names = "-in", description = "Input file name")
    private String inputFile;

    public String getInputFile() {
        return inputFile;
    }

}

