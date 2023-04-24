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
            if (s.charAt(i) == ' ')
                ss.append(ConsoleColors.RESET).append("\n");
        }
        ss.append(ConsoleColors.RESET).append("\n");
        System.out.println(ss);
        return ss;
    }

    public static void main(String[] args) {
        SpriteConverter sc = new SpriteConverter();
        sc.SpriteStringConverter(
                "GGGGGGGGGGGGGGGGGGGGGGGG GGGGGGGGGGGGGBBGGGBBGGGG GGGGGBBGGGGGBBPBGBBPBGGG GGGGGBBBGGGBAACAAAACAGGG GGGGGGBBGGBAAAAAAAAAAAGG GGGGGGBBGGBAAAABBBBAAAGG GGGGBBBBGGBAABDBBBBDBAGG GGGBBBBGGGWWABDBBBBDBAWG GGGBBGGGGGBAAAAAACAAAAGG GGGBBGGGGGWWAAAAWAWAAAWG GGGAAAAAAAARRAAAAAAAAGGG GGGAAAAAAAAAARRRRRRRGGGG GGGAAAAAAAAAAAAAADAAGGGG GGGAAAAAAAAAAAAAAAAAGGGG GGGAAAAAAAAAAAAAAAAAGGGG GGBBAAAAAAAAAAAAAAAAGGGG GBBBAAAAAAAAAAAAAAAAAGGG GBBBBAAAAAAAAAAAAAAAAGGG GBBBBBAAAAAAAAAAAAAAAGGG GBBBGGGAAAAGAAAGGGAAAAGG GBBBGGGGAAAGAAAGGGGAAAGG GGWWGGGGGWWGGWWGGGGGWWGG GGGGGGGGGGGGGGGGGGGGGGGG GGGGGGGGGGGGGGGGGGGGGGGG");
        sc.SpriteStringConverter(
                "CCCCCCCCCCCCCCCCCCCCCCCC CCCCCCCCCCCCCGGGGGGGCCCC CCCCCCCCCCCGGGGGEEEGGCCC CCCCCCCCCCEFEFEEEEEGGCCC CCCCCCCCEEEEFEEEEFEGGGCC CCCCCCCEEFEEFEFEEEEEEGCC CCCCEEEEFEEEFEFHEEEEEGCC CCCFEEEGEFEIEEFEHEEEEGCC CCFFFFFEEEEEFHEEHEEEECCC CCCFFFEEFEFFFHHHEEICCCCC CCCCCFFFGCCCCHHHHCCCCCCC CCCCCCCCCCCCCIHCCCCCCCCC CCCCCCCCCCCCIIHCCCCCCCCC CCCCCCCCCCCCCIHCCCCCCCCC CCCCCCCCCCCCIIHCCCCCCCCC CCCCCCIHCCCCIHIHHICCCCCC CCCCCHHHIIHIHHHIHHICCCCC CBBBBBBBBBBBBBBBBBBBBBCC CCBAAAAWWWWWWWWWWWWWBCCC CCBBAAAAAAAAAAAAAAABBCCC CCCBAAAAAAAWWWWWWWWBCCCC CCCBABBBBBBBBBBBBBWBCCCC CCCBBBCCCCCCCCCCCBBBCCCC CCCCCCCCCCCCCCCCCCCCCCCC");
    }
}
