package edu.ucla.nesl.mca.xdr;

import java.io.IOException;

/**
 * Interface for read and write XDR files
 * @author cgshen
 *
 */
public interface XDRSerializable {
    /**
     * Write data structure to XDR file
     * @param output
     */
    public void writeXDR(XDRDataOutput out) throws IOException;
    
    /**
     * Read data structure from XDR file
     * @param input
     */
    public void readXDR(XDRDataInput in) throws IOException;
}