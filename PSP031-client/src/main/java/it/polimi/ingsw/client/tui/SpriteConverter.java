package it.polimi.ingsw.client.tui;

public class SpriteConverter {
    public SpriteConverter() {
    }

    public StringBuilder SpriteStringConverter(String s) {
        StringBuilder ss = new StringBuilder();
        ss.append(ConsoleColors.RESET);
        for (int i = 0; i < 599; i++) {
            ss.append(ConsoleColors.RESET);
            if (s.charAt(i) == 'B')
                ss.append(ConsoleColors.BLACK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'G')
                ss.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'E')
                ss.append(ConsoleColors.GREEN_BACKGROUND).append("   ");
            if (s.charAt(i) == 'F')
                ss.append(ConsoleColors.GREEN_DARK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'H')
                ss.append(ConsoleColors.BROWN_BACKGROUND).append("   ");
            if (s.charAt(i) == 'I')
                ss.append(ConsoleColors.BROWN_DARK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'A')
                ss.append(ConsoleColors.BLACK_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'P')
                ss.append(ConsoleColors.PURPLE_BACKGROUND).append("   ");
            if (s.charAt(i) == 'C')
                ss.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'R')
                ss.append(ConsoleColors.RED_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'W')
                ss.append(ConsoleColors.WHITE_BACKGROUND).append("   ");
            if (s.charAt(i) == 'D')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'K')
                ss.append(ConsoleColors.RED_DARK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'L')
                ss.append(ConsoleColors.RED_VERY_DARK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'M')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'O')
                ss.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'N')
                ss.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'S')
                ss.append(ConsoleColors.ORANGE_BACKGROUND).append("   ");
            if (s.charAt(i) == 'Q')
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'T')
                ss.append(ConsoleColors.BLUE_BACKGROUND).append("   ");
            if (s.charAt(i) == 'U') // should be pink but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append("   ");
            if (s.charAt(i) == 'J') // should be brown bright but not working
                ss.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append("   ");
            if (s.charAt(i) == 'X') // should be brown bright but not working
                ss.append(ConsoleColors.PINK_BACKGROUND).append("   ");
            if (s.charAt(i) == ' ')
                ss.append(ConsoleColors.RESET).append("\n");
        }
        ss.append(ConsoleColors.RESET).append("\n");
        System.out.println(ss);
        return ss;
    }

    public static void main(String[] args) {
        SpriteConverter sc = new SpriteConverter();
        //CAT 
        sc.SpriteStringConverter("""
                GGGGGGGGGGGGGGGGGGGGGGGG
                 GGGGGGGGGGGGGBBGGGBBGGGG
                 GGGGGBBGGGGGBBPBGBBPBGGG
                 GGGGGBBBGGGBAACAAAACAGGG
                 GGGGGGBBGGBAAAAAAAAAAAGG
                 GGGGGGBBGGBAAAABBBBAAAGG
                 GGGGBBBBGGBAABDBBBBDBAGG
                 GGGBBBBGGGWWABDBBBBDBAWG
                 GGGBBGGGGGBAAAAAACAAAAGG
                 GGGBBGGGGGWWAAAAWAWAAAWG
                 GGGAAAAAAAARRAAAAAAAAGGG
                 GGGAAAAAAAAAARRRRRRRGGGG
                 GGGAAAAAAAAAAAAAADAAGGGG
                 GGGAAAAAAAAAAAAAAAAAGGGG
                 GGGAAAAAAAAAAAAAAAAAGGGG
                 GGBBAAAAAAAAAAAAAAAAGGGG
                 GBBBAAAAAAAAAAAAAAAAAGGG
                 GBBBBAAAAAAAAAAAAAAAAGGG
                 GBBBBBAAAAAAAAAAAAAAAGGG
                 GBBBGGGAAAAGAAAGGGAAAAGG
                 GBBBGGGGAAAGAAAGGGGAAAGG
                 GGWWGGGGGWWGGWWGGGGGWWGG
                 GGGGGGGGGGGGGGGGGGGGGGGG
                 GGGGGGGGGGGGGGGGGGGGGGGG
                 """);
        //TREE 
        sc.SpriteStringConverter("""
                CCCCCCCCCCCCCCCCCCCCCCCC
                 CCCCCCCCCCCCCGGGGGGGCCCC
                 CCCCCCCCCCCGGGGGEEEGGCCC
                 CCCCCCCCCCEFEFEEEEEGGCCC
                 CCCCCCCCEEEEFEEEEFEGGGCC
                 CCCCCCCEEFEEFEFEEEEEEGCC
                 CCCCEEEEFEEEFEFHEEEEEGCC
                 CCCFEEEGEFEIEEFEHEEEEGCC
                 CCFFFFFEEEEEFHEEHEEEECCC
                 CCCFFFEEFEFFFHHHEEICCCCC
                 CCCCCFFFGCCCCHHHHCCCCCCC
                 CCCCCCCCCCCCCIHCCCCCCCCC
                 CCCCCCCCCCCCIIHCCCCCCCCC
                 CCCCCCCCCCCCCIHCCCCCCCCC
                 CCCCCCCCCCCCIIHCCCCCCCCC
                 CCCCCCIHCCCCIHIHHICCCCCC
                 CCCCCHHHIIHIHHHIHHICCCCC
                 CBBBBBBBBBBBBBBBBBBBBBCC
                 CCBAAAAWWWWWWWWWWWWWBCCC
                 CCBBAAAAAAAAAAAAAAABBCCC
                 CCCBAAAAAAAWWWWWWWWBCCCC
                 CCCBABBBBBBBBBBBBBWBCCCC
                 CCCBBBCCCCCCCCCCCBBBCCCC
                 CCCCCCCCCCCCCCCCCCCCCCCC
                """);
        //BOOK
        sc.SpriteStringConverter("""
                MMMMMMMMMMMMMMMMMMMMMMMM
                 MMMMMMMMMMMMMMMMMMMMMMMM
                 MMMKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKMMKMKMKMMKMKKMMRMM
                 MMLKLKMKKMKMKMKKMKKMKRMM
                 MMLKLKMMKMMMKMMKMKKMMRMM
                 MMLKLKKMKMKMKMKKMKKMKRMM
                 MMLKLKMMKMKMKMMKMMKMKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLKLKKKKKKKKKKKKKKKKRMM
                 MMLRLRRRRRRRRRRRRRRRRRMM
                 MMLLLLLLLLLLLLLLLLLLLLMM
                 MMLWWWWWWWWWWWWWWWWWWLMM
                 MMLWMMMMRRRRRMMMMMMMMLMM
                 MMLWMMMMRRRRRMMMMMMMMLMM
                 MMLWMMMMMMMMMMMMMMMMMLMM
                 MMLKKKKKKKKKKKKKKKKKKKMM
                 MMMLLLLLLLLLLLLLLLLLLLMM
                 MMMMMMMMMMMMMMMMMMMMMMMM
                 MMMMMMMMMMMMMMMMMMMMMMMM
                                 """);
        //TROPHY
        sc.SpriteStringConverter("""
                NNNNNNNNNNNNNNNNNNNNNNNN
                 NNNNNNNNNNNNNNNNNNNNNNNN
                 NNNNNNNNNNNNNNNNNNNNNNNN
                 NNNNNSSSSSOOOOQQOONNNNNN
                 NNNNNNSSSSSSSSSSSNNNNNNN
                 NNSQQOOSSSOOOOQOOOOQQNNN
                 NNSQNNSSSSOOOOQOONNSQNNN
                 NNSQNNSSSSOOOOQOONNSONNN
                 NNSONNSSSSOOOOQOONNSQNNN
                 NNSONNSSSSOOOOQOONNSQNNN
                 NNSONNSSSSOOOOQOONNSONNN
                 NNSONNSSSSOOOOQOONNSONNN
                 NNSOOOOSSSOOOOQOOOOOONNN
                 NNNNNNSSSSOOOOQOONNNNNNN
                 NNNNNNSSSSOOOOQOONNNNNNN
                 NNNNNNSSSSOOOOOOONNNNNNN
                 NNNNNNNSSSSSSSSSNNNNNNNN
                 NNNNNNNNNNSSSNNNNNNNNNNN
                 NNNNNNNNNNSOQNNNNNNNNNNN
                 NNNNNNNNNNSOQNNNNNNNNNNN
                 NNNNNNNNNSSOOQNNNNNNNNNN
                 NNNNNNNSSSOOOQOONNNNNNNN
                 NNNNNSSSSOOOOOQOOONNNNNN
                 NNNNNNNNNNNNNNNNNNNNNNNN""");
        //GAME
        sc.SpriteStringConverter("""
                OOOOOOOOOOOOOOOOOOOOOOOO
                 OOOOOOOOOOOOOOJJJJJJJJOO
                 OOOOOOOOOOOOOJBSSSSSSBHO
                 OOOOOOOOOOOOOJSSJJJJSSHO
                 OOOOOOOOOOOOOJSSJSSJSSHO
                 OOOOOOOOOOOOOJSSSSSJSSHO
                 OOOOOOOOOOOOOJSSSSJSSSHO
                 OOOOOOOOOOOOOJUUUSJSSSHO
                 OOOOOOORRRRROJUUUSSSSSHO
                 OOOOOORRRRRRRRRUUSJSSBHO
                 OOOOOOHHHUUBUOTTTHHHHHOO
                 OOOOOHUHUUUBUUTTTOOOOOOO
                 OOOOOHUHHUUUHUUTTOOOOOOO
                 OOOOOHHUUUUHHHHTOOOOOOOO
                 OOOOOOOUUUUUUUTOOOOOOOOO
                 OOOTTTTTRTTTRTOOOOOOOOOO
                 OOTTTTTTTRTTTROOBOOOOOOO
                 OUUTTTTTTRRRRROOBOOOOOOO
                 OUUUORRTRRORRORBBOOOOOOO
                 OOUOBRRRRRRRRRRBBOOOOOOO
                 OOOBBBRRRRRRRRRBBOOOOOOO
                 OOBBBRRRRRRROOOOOOOOOOOO
                 OOBOORRRROOOOOOOOOOOOOOO
                 OOOOOOOOOOOOOOOOOOOOOOOO
                """);
        //FRAME
        sc.SpriteStringConverter("""
                TTTTTTTTTTTTTTTTTTTTTTTT
                 TTTTTTTTTTTTTTTTTTTTTTTT
                 TTTTTTTTTTTTTTTTTTTTTTTT
                 TTJJJJJJJJJJJJJJJJJJJTTT
                 TTJJJJJJJJJJJJJJJJJJJTTT
                 TTIJJIIIIIIIIIIIIIIJJTTT
                 TTIJJHIJJJJJJJJJJIIJJTTT
                 TTIJJHJIIIIIIIIIIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHXXXXXXXXIJIJJTTT
                 TTIJJHJHHHHHHHHHIJIJJTTT
                 TTIJJHJJJJJJJJJJJIIJJTTT
                 TTIJJHHHHHHHHHHHHHIJJTTT
                 TTJJJJJJJJJJJJJJJJJJJTTT
                 TTJJJJJJJJJJJJJJJJJJJTTT
                 TTTTTTTTTTTTTTTTTTTTTTTT
                 TTTTTTTTTTTTTTTTTTTTTTTT
                 TTTTTTTTTTTTTTTTTTTTTTTT
                 """);
    }
}
