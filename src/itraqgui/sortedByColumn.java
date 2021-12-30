/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;


import java.util.Arrays;
import java.util.Comparator;

public class sortedByColumn {

    public static void main(final String[] args) {
        final String[][] data = new String[][] {
                new String[] { "2009.07.25 20:24", "Message A" },
                new String[] { "2009.07.25 20:17", "Message G" },
                new String[] { "2009.07.25 20:25", "Message B" },
                new String[] { "2009.07.25 20:30", "Message D" },
                new String[] { "2009.07.25 20:01", "Message F" },
                new String[] { "2009.07.25 21:08", "Message E" },
                new String[] { "2009.07.25 19:54", "Message R" } };

        Arrays.sort(data, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                final String time1 = entry1[0];
                final String time2 = entry2[0];
                return time1.compareTo(time2);
            }
        });

        for (final String[] s : data) {
            System.out.println(s[0] + " " + s[1]);
        }
        
        int[][] temp = { { 1, 50, 5 }, { 2, 30, 8 }, { 3, 90, 6 },
        { 4, 20, 7 }, { 5, 80, 9 }, };
        Arrays.sort(temp, new Comparator<int[]>() {
        @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o2[1], o1[1]);
            }
        });
    }

}