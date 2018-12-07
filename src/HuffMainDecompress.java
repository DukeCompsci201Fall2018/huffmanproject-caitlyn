import java.io.File;
import java.io.*;

public class HuffMainDecompress {
	private static final int PSUEDO_EOF = 0;
	private static final int BITS_PER_INT = 0;
	private static final int BITS_PER_WORD = 0;

	public static void main(String[] args) {
		
		System.out.println("Huffman Decompress Main");
		
		File inf = FileSelector.selectFile();
		File outf = FileSelector.saveFile();
		if (inf == null || outf == null) {
			System.err.println("input or output file cancelled");
			return;
		}
		BitInputStream bis = new BitInputStream(inf);
		BitOutputStream bos = new BitOutputStream(outf);
		HuffProcessor hp = new HuffProcessor();
		hp.decompress(bis, bos);
		System.out.printf("uncompress from %s to %s\n", 
				           inf.getName(),outf.getName());		
		
		System.out.printf("file: %d bits to %d bits\n",inf.length()*8,outf.length()*8);
		System.out.printf("read %d bits, wrote %d bits\n", 
				           bis.bitsRead(),bos.bitsWritten());
		long diff = 8*(outf.length() - inf.length());
		long diff2 = bos.bitsWritten() - bis.bitsRead();
		System.out.printf("%d compared to %d\n",diff,diff2);
	}
	
	
	
	public void decompress(BitInputStream in, BitOutputStream out) {
		
		int bits = in.readBits(BITS_PER_INT);
		if(bits !=BITS_PER_INT) {
			throw new HuffException("illegal header starts with "+bits);
			
		}
		HuffNode root = readTreeHeader(in);
		readCompressedBits(root,in,out);
		out.close();
	}
	
	public HuffNode readTreeHeader(BitInputStream in) {


		int bit = in.readBits(BITS_PER_WORD);
		
		HuffNode root = new HuffNode(BITS_PER_WORD+1,0);
		
		
		if (bit == -1) throw new HuffException("illegal bit");
		
		if (bit == 0) {
			root.myLeft = readTreeHeader(in);
			root.myRight = readTreeHeader(in);
					return new HuffNode(0,0,root.myLeft,root.myRight);
		}
		else {
			int value = in.readBits(9);
					return new HuffNode(value,0,null,null);
		
		}
	}
	
		public void readCompressedBits(HuffNode root, BitInputStream in, BitOutputStream out) {

			//HuffNode end = new HuffNode(PSUEDO_EOF,0);
			
			
			HuffNode current = root; 
			while (true) {
				int bits = in.readBits(1);
				if (bits == -1) {
					throw new HuffException("bad input, no PSEUDO_EOF");
				}
				else { 
					if (bits == 0) current = current.myLeft;
					else current = current.myRight;

					if (bits == 1) {
						if (current.myValue == PSUEDO_EOF) 
							break;   // out of loop
						else {
							current.myValue = in.readBits(9);
									current = root; // start back after leaf
						}
					}
				}
			}



		}


		}
