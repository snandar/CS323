public class BWT
{
    // Utility method: die with an error message.
    static void die(String msg) { throw new RuntimeException(msg); }
    static int R = 256;
    
    // The forward Burrows-Wheeler Transform.
    // The mark char should not appear in the input.
    static String transform(String input, char mark)
    {
        if (input.indexOf(mark) >= 0)
            die("input should not contain mark");
        // TODO: replace this slow method with Manber instead.
        
        StringBuilder word = new StringBuilder(input);
        word.append(mark);
        
        innerManber m = new innerManber();
        m.manber(word.toString());
        
        int[] manber_seq = new int[input.length()+1];
        for(int i=0; i<m.N;i++){
        	manber_seq[i] = m.index[i];
        }
       
        word.append(word.toString());
        String s = word.substring(input.length(), input.length()*2+1);
        
        StringBuilder sortedword = new StringBuilder(s);
        
        StringBuilder result = new StringBuilder();
        for(int i=0; i<input.length()+1 ; i++){
        	result.append(sortedword.charAt(manber_seq[i]));
        }
        

        return result.toString();
    }

    // The reverse Burrows-Wheeler Transform.
    // The mark char should appear once in bw.
    static String transformBack(String bw, char mark)
    {
        int at = bw.indexOf(mark), size = bw.length();
        if (at < 0)
            die("bw should contain mark");
        if (bw.indexOf(mark, at+1) >= 0)
            die("bw should not contain mark twice");
        
        int[] T = new int[size];
        char[] F = new char[size];
        char[] L = new char[size];
        
        for (int j=0; j<size; j++){
        	L[j] = bw.charAt(j);
        }
        
        T = sort(L, F);

        StringBuilder resultreverse = new StringBuilder();
        // Remember to use a StringBuilder, not concatenation, to
        // build the final answer.
        
        int i = T[at];
        
        int[] sequence = new int[size];
        for(int k=0; k<size ; k++){
        	sequence[k] = i;
        	i = T[i];
        }
        
        for(int k=1; k<size ; k++){
        	resultreverse.append(F[sequence[k]]);
        }
        
        return resultreverse.reverse().toString();
    }

    // main() expects two command line arguments, STRING and MARK,
    // where MARK is a single char.  If the MARK does not appear in
    // STRING, then we do the forward transform.  If it does appear,
    // then we do the reverse, transformBack.
    //
    // Examples:
    //    java BWT banana %
    //    java BWT annb%aa %
    //
    public static void main(String[] args)
    {
        if (args.length != 2)
            die("expected two arguments: STRING MARK");
        String string = args[0];
        String markStr = args[1];
        if (markStr.length() != 1)
            die("MARK should be a single char");
        char mark = markStr.charAt(0);
        if (string.indexOf(mark) < 0) {
            System.out.print ("tranform: ");
            System.out.println(transform(string, mark));
        } else {
            System.out.print ("tranformBack: ");
            System.out.println(transformBack(string, mark));
        }
    }
    
    //CharSort adaptation
    static int[] sort(char[] input, char[] output)
    {
        int N = input.length;
        assert output.length >= N; // long enough to receive answer

        // compute frequency counts
        int[] count = new int[R+1];
        for (int i = 0; i < N; ++i)
            ++count[input[i] + 1];

        // Now for each char code c, count[c+1] equals the number
        // of chars in the input with code equal to c.

        // compute cumulative counts
        for (int r = 0; r < R; ++r)
            count[r+1] += count[r];

        // Now for each char code c, count[c] equals the number
        // of chars in the input with code strictly less than c.
        assert count[0] == 0;
        assert count[R] == N;
        int[] T = new int[N];
        
        // Finally, move each char from the input to the output.
        // For BWT, this is where you finally figure out T.
        for (int i = 0; i < N; ++i){
        	T[i] = count[input[i]];
            output[count[input[i]]++] = input[i];
            
        }
            
        return T;
    }
    
}

class innerManber
{
    // We assume all char codes are in extended ASCII range (0 to 255).
    static int R = 256;

    static int N;               // length of input string
    static String text;         // input text
    static int[] index;         // offset of ith string in order
    private static int[] rank;          // rank of ith string
    private static int[] newrank;       // rank of ith string (temporary)
    private static int offset;

    void manber(String s)
    {
        N    = s.length();
        text = s;
        index   = new int[N+1];
        rank    = new int[N+1];
        newrank = new int[N+1];

        // sentinels

        index[N] = N;
        rank[N] = -1;

        msd();
        doit();
        
    }

    // do one pass of msd sorting by rank at given offset
    private static void doit()
    {
        for (offset = 1; offset < N; offset += offset)
        {
            // System.out.println("offset = " + offset);
            int count = 0;
            for (int i = 1; i <= N; i++) {
                if (rank[index[i]] == rank[index[i-1]]) count++;
                else if (count > 0) {
                    // sort
                    int left = i-1-count;
                    int right = i-1;
                    quicksort(left, right);

                    // now fix up ranks
                    int r = rank[index[left]];
                    for (int j = left + 1; j <= right; j++) {
                        if (less(index[j-1], index[j]))  {
                            r = rank[index[left]] + j - left;
                        }
                        newrank[index[j]] = r;
                    }

                    // copy back - note can't update rank too eagerly
                    for (int j = left + 1; j <= right; j++) {
                        rank[index[j]] = newrank[index[j]];
                    }

                    count = 0;
                }
            }
        }
    }

    // sort by leading char
    private void msd()
    {
        // calculate frequencies
        int[] freq = new int[R];
        for (int i = 0; i < N; i++)
            freq[text.charAt(i)]++;

        // calculate cumulative frequencies
        int[] cumm = new int[R];
        for (int i = 1; i < R; i++)
            cumm[i] = cumm[i-1] + freq[i-1];

        // compute ranks
        for (int i = 0; i < N; i++)
            rank[i] = cumm[text.charAt(i)];

        // sort by first char
        for (int i = 0; i < N; i++)
            index[cumm[text.charAt(i)]++] = i;
    }


    /**********************************************************************
     *  Helper functions for comparing suffixes.
     **********************************************************************/

    /**********************************************************************
     * Is the substring text[v+offset .. N] lexicographically less than the
     * substring text[w+offset .. N] ?
     **********************************************************************/
    private static boolean less(int v, int w)
    {
        assert rank[v] == rank[w];
        if (v + offset >= N) v -= N;
        if (w + offset >= N) w -= N;
        return rank[v + offset] < rank[w + offset];
    }

    /*************************************************************************
     *  Quicksort code from Sedgewick 7.1, 7.2.
     *************************************************************************/

    private static void exch(int i, int j)
    {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    static void quicksort(int l, int r)
    {
        if (r <= l) return;
        int i = partition(l, r);
        quicksort(l, i-1);
        quicksort(i+1, r);
    }

    static int partition(int l, int r)
    {
        int i = l-1, j = r;
        int v = index[r];       // could use random pivot here
        while (true)
        {
            while (less(index[++i], v))
                ;
            while (less(v, index[--j]))
                if (j == l) break;
            if (i >= j) break;
            exch(i, j);
        }
        exch(i, r);
        return i;
    }
}
