
import java.io.*;
import java.util.*;

public class CompressedSuffixTrie
 {
	public static String charSet = "ACGT";
	private static char END_CHAR = '$';
	
	private String input;
	private TrieNode root;
	
	/**
	 * Create a compressed suffix trie from file.
	 * about the big O:
	 * the big O of this method is the big O of method initTrie(),
	 * which is O(n^2) where n is the length of input string.
	 * please see the comment of method initTrie().
	 * the big O of this method is: O(n^2).
	 * @param f
	 * @throws Exception
	 */
	public CompressedSuffixTrie( String f ) throws Exception
	{ 
		//read out the ADT string from file. Delete any illegal chars.
		this.input = readFile(f);// + END_CHAR;
		//Initiate the trie.
		this.initTrie();
	}

	/**
	 * match the pattern with ADT string, return the index of the first match.
	 * about the big O:
	 * basically this method deal with each char in the pattern string one by one.
	 * there is 1 do-while structure which in worse case run for n time
	 * where n is the length of the pattern, because at most there are n nodex in the
	 * trie.
	 * The big O is: O(n)
	 * @param s
	 * @return
	 */
	public int findString( String s )
	{ 
		//input string is empty, return fail.
		if(s == null || s.isEmpty())
			return -1;
		String pattern = s;
		TrieNode node = this.root;
		do {
			//get first char matched node from all children
			TrieNode matchChild = node.getFirstCharMatchChild(pattern);
			//no match child, return fail.
			if(matchChild == null)
				return -1;
			int lLength = matchChild.endIndex - matchChild.startIndex + 1;
			int pLength = pattern.length();
			//compare the length of match string and input string
			if(pLength <= lLength) {//if pattern string has shorter or equal length, means last node found to check.
				String lText = matchChild.getString().substring(0, pLength);
				if(lText.equals(pattern)) //match, return index
					return matchChild.startIndex + pLength - s.length();
				else //not match, return fail.
					return -1;
			}
			else {//or pattern string has longer length, campare with current node
				String pText = pattern.substring(0, lLength);
				if(!pText.equals(matchChild.getString())) { //not match, return fail.
					return -1;
				}
				else { //or set node as parent, go to next child match.
					pattern = pattern.substring(lLength, pLength);
					node = matchChild;
				}
			}
		}while(!node.isExternal());
		return -1;
	}

	/**
	 * find the LCS of 2 strings and output into a file.
	 * about the big O:
	 * first the method generate a LCS table using dynamic programming method.
	 * the big O of LCSTable() method is O(m*n). please see the comment of LCSTable().
	 * second the method get the LCS string from the LCS table by calling
	 * method getLCS(). the big O of method getLCS() is O(m*n). please see the comment
	 * of the method getLCS(). there are no more complex method calls but single 
	 * operations.
	 * the big O is: O(m*n + m*n + a) = O(m*n).
	 * @param f1
	 * @param f2
	 * @param f3
	 * @return
	 * @throws Exception
	 */
	public static float similarityAnalyser(String f1, String f2, String f3) throws Exception
	{ 
		String text1 = readFile(f1);
		String text2 = readFile(f2);
		LCSTable lcs = new LCSTable(text1, text2);
		String text = lcs.getLCS();
		writeFile(f3, text);
		float lcsLength = text.length();
		float maxLength = text1.length() > text2.length() ? text1.length() : text2.length();
		return lcsLength / maxLength;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * initiate the trie.
	 * About the big O:
	 * the method get all the suffixes of the input and add them into trie.
	 * after that the compress operation is called for 1 time.
	 * there is 1 for structure in the method for add suffixes n time where
	 * n is the length of the input string.
	 * the big O of method addSuffix() is O(m) where m is length of a suffix
	 * ranged for 1 to n. Please see the comment of the method addSuffix().
	 * the big O of the for structure is O(1 + 2 + 3 +...+ n) = O(n^2)
	 * the big O of method compress() is O(n) where n is the length of the input,
	 * please see the comment of the method compress().
	 * so the big O of this method is: O(n^2).
	 */
	private void initTrie() {
		//create root node.
		this.root = new TrieNode();
		String str = this.input;
		int length = str.length();
		//add all the suffixes into root node to initiate the trie.
		for(int pos = 0; pos < length; pos++) {
			//get a suffix
			String suffix = str.substring(pos, length);
			this.addSuffix(pos,suffix);
		}
		//compress the trie
		this.root.compress();
	}
	
	/**
	 * add a suffix into trie.
	 * About the big O:
	 * the method deal with a suffix of the input string by check and add a char of it one by one.
	 * there is 1 for structure in it. No complex method calls.
	 * The Big O is: O(n) where n is the length of a suffix.
	 * @param startIndex
	 * @param suffix
	 */
	private void addSuffix(int startIndex, String suffix) {
		int length = suffix.length();
		//set root node as parent node first.
		TrieNode parent = this.root;
		//deal with each char.
		for(int pos = 0; pos < length; pos++) {
			//get the char one by one
			String c = suffix.substring(pos, pos + 1);
			//check if the char has been added into this node's children.
			TrieNode child = parent.getChild(c);
			//if not
			if(child == null) {
				//create a new node
				child = new TrieNode(parent, startIndex + pos, startIndex + pos);
				//add it to parent.
				parent.addChild(child);
			}
			//if added, set the child as parent and goto next char.
			parent = child;
		}
	}
	
	
	
	
	
	/**
	 * read string from file. filter all illegal chars.
	 * big O is: O(n) where n is the length of input file length.
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static String readFile(String fileName) throws Exception {
		String input = "";

		InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName));
        int tempChar;
        while ((tempChar = reader.read()) != -1) {
            char c = (char)tempChar;
        	if(checkChar(c)) {
            	input = input + c;
            }
        }
        reader.close();
        return input;
	}
	
	/**
	 * write string into a file.
	 * the big O is: O(n) where n is the length of output string.
	 * @param path
	 * @param text
	 * @throws IOException
	 */
	private static void writeFile(String path, String text) throws IOException {
		File file = new File(path);
		if(!file.exists())
			file.createNewFile();
		FileWriter fw = new FileWriter(file);
		fw.write(text);
		fw.flush();
		fw.close();
	}
	
	private static boolean checkChar(char ch) {
		return charSet.lastIndexOf(ch) != -1;
	}
	
	
	
	
	
	
	
//	 public static void main(String args[]) throws Exception {
//	            
//		 CompressedSuffixTrie trie = new CompressedSuffixTrie("sample1.txt"); 
//		 System.out.println("CAACT is at: " + trie.findString("CAACT"));
//		 System.out.println("GAAG is at: " + trie.findString("GAAG"));
//		 
//
//		 CompressedSuffixTrie trie1 = new CompressedSuffixTrie("file1.txt");
//	        
//		 System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));
//
//		 System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));
//		         
//		 System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));
//		         
//		 System.out.println(CompressedSuffixTrie.similarityAnalyser("file2.txt", "file3.txt", "file4.txt"));
//	 }
	 
	 
	 
	 private class TrieNode {
			private TrieNode parrent = null;
			private List<TrieNode> children = new ArrayList<TrieNode>();
			private int startIndex = -1;
			private int endIndex = -1;
			
			public TrieNode() {
			}
			
			public TrieNode(TrieNode parrent, int startIndex, int endIndex) {
				this.parrent = parrent;
				this.startIndex = startIndex;
				this.endIndex = endIndex;
			}
			
			public boolean isRoot() {
				return this.parrent == null;
			}
			
			public boolean isExternal() {
				return this.children.size() == 0;
			}
			
			public void addChild(TrieNode child) {
				this.children.add(child);
			}
			
			public List<TrieNode> getChildren() {
				return this.children;
			}
			
			/**
			 * compress the trie.
			 * about the big O:
			 * the method check every child nodes and remove the node which is only child
			 * of the parent. The check operations will run n times where n is the count of
			 * the nodes and also the char number of the input string.
			 * in worse case there are a*n operations where a is constant value.
			 * so the big O of this method is: O(n).
			 */
			public void compress() {
				int childrenCount = this.children.size();
				//if there is no child nodes, return.
				if(childrenCount == 0) {
					return;
				}
				//if there is only 1 child node, remove it after reset the parent end index.
				else if(childrenCount == 1) {
					TrieNode child = this.children.get(0);
					this.endIndex = child.endIndex;
					List<TrieNode> childChildren = child.getChildren();
					this.children = childChildren;
					this.compress();
				}
				//if there are more than 1 child nodes, call the compress method of all of them.
				else {
					for(int pos = 0; pos < childrenCount; pos++) {
						this.children.get(pos).compress();
					}
				}
			}
			
			
			
			private TrieNode getChild(String text) {
				for(int pos = 0; pos < this.children.size(); pos++) {
					TrieNode child = this.children.get(pos);
					if(text.equals(child.getString()))
						return child;
				}
				return null;
			}
			
			private TrieNode getFirstCharMatchChild(String text) {
				String firstChar = text.substring(0, 1);
				for(int pos = 0; pos < this.children.size(); pos++) {
					TrieNode child = this.children.get(pos);
					if(firstChar.equals(child.getString().substring(0, 1)))
						return child;
				}
				return null;
			}
			
			
			private String getString() {
				if(this.startIndex == -1 || this.endIndex == -1)
					return "root";
				return input.substring(startIndex, endIndex + 1);
			}
			
			
			private List<String> getSuffixString() {
				List<String> list = new ArrayList<String>();
				String text = this.isRoot() ? "" : this.getString();
				if(this.children.size() > 0) {
					for(int pos = 0; pos < this.children.size(); pos++) {
						List<String> sublist = this.children.get(pos).getSuffixString();
						for(int subpos = 0; subpos < sublist.size(); subpos++) {
							list.add(text + sublist.get(subpos));
						}
					}
				}
				else
					list.add(text);
				return list;
			}
		}

	 
	 static class LCSTable {
			private int[][] table;
			private String text1;
			private String text2;
			
			/**
			 * generate LCS table.
			 * about the big O:
			 * there is 2 for structure one in the other.
			 * internal block will run for m*n time where m is the length of first input string
			 * and n is the length of the seconde string. 
			 * in the internal block there is no more complex structure of method calls. 
			 * operation count are 3 or 5.
			 * the big O is O(m*n).
			 * @param text1
			 * @param text2
			 */
			public LCSTable(String text1, String text2) {
				this.text1 = text1;
				this.text2 = text2;
				//get the lengths of 2 input strings
				int t1Length = text1.length();
				int t2Length = text2.length();
				//initiate table with value 0.
				//do not need to set value because the default value 0 is used.
				this.table = new int[t1Length + 1][t2Length + 1];
				for(int i = 0; i < t1Length; i++) {
					for(int j = 0; j < t2Length; j++) {
						//get chars in location i, j of string1 and string2
						char c1 = text1.charAt(i);
						char c2 = text2.charAt(j);
						if(c1 == c2) {//if chars are equals
							//set i, j with value i - 1, j - 1 added by 1
							this.setValue(i, j, this.getValue(i - 1, j - 1) + 1);
						}
						else {// if not equal.
							int v1 = this.getValue(i - 1, j);
							int v2 = this.getValue(i, j - 1);
							//compare bigger value of i - 1, j and i, j - 1
							//set the bigger one to location i, j.
							if(v1 >= v2) {
								this.setValue(i, j, v1);
							}
							else {
								this.setValue(i, j, v2);
							}
						}
					}
				}
			}
			
			/**
			 * get LCS string from a LCS table sized (n+1) * (m+1)
			 * about the big O:
			 * there is 1 while structure in the method which at most could be run
			 * for m*n time where m is the length of input string 1 and n is the length
			 * of the input string 2. Inside the while structure there is no complex
			 * method calls, all single operations (6 operations).
			 * the big O is: O(m*n).
			 * @return
			 */
			public String getLCS() {
				//get length of 2 input strings
				int i = this.text1.length() - 1;
				int j = this.text2.length() - 1;
				
				String output = "";
				//compare every char of string 1 and 2.
				while(i > -1 && j > -1) {
					//get chars from 2 strings.
					char c1 = this.text1.charAt(i);
					char c2 = this.text2.charAt(j);
					//if chars are equal
					if(c1 == c2) {
						//add char to output string,
						output = c1 + output;
						//decrease both index with 1.
						i--;
						j--;
					}// if chars are not equal,
					else {
						
						int v1 = this.getValue(i - 1, j);
						int v2 = this.getValue(i, j - 1);
						if(v1 > v2) {//compare the value of the LCS table in i-1, j and i, j-1,
							i--;//if i-1, j is bigger, decrease i.
						}
						else {//else decrease j.
							j--;
						}
					}
				}
				return output;
			}
			
			private void setValue(int i, int j, int value) {
				this.table[i + 1][j + 1] = value;
			}
			
			private int getValue(int i, int j) {
				return this.table[i + 1][j + 1];
			}
			
			
			
			
			private void print() {
				for(int i = 0; i < this.table.length; i++) {
					for(int j = 0; j < this.table[i].length; j++) {
						System.out.print(this.table[i][j] + "\t");
					}
					System.out.println();
				}
			}
		}
}




