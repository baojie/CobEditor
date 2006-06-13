package edu.iastate.utils.string;

/**
 * <p>Title: PatternEx</p>
 * <p>Description: Some regular expression routines </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0 - 2003-11-07
 *
 * note:
 * CharBuffer, String, StringBuffer are all implement of CharSequence interface
 *
 */
public class PatternEx
{
//  Characters
    public static String BACKSLASH = "\\"; // The backslash character
    public static String TABLE = "\\t"; // The tab character ('\u0009')
    public static String LINEFEED = "\\n"; // The newline (line feed) character ('\\u000')
    public static String RETURN = "\\r"; // The carriage-return character ('\\u000')
    public static String FORMFEED = "\\f"; // The form-feed character ('\u000C')
    public static String BELL = "\\a"; // The alert (bell) character ('\u0007')
    public static String ESCAPE = "\\e"; // The escape character ('\u001B')
    public static String CONTROL = "\\c"; // \cx The control character corresponding to x

//  Character classes  , [abc] a , b , or c( simple class )
    public static String IN_SET(char[] chars)
    {
        String total = new String();
        for (int i = 0; i < chars.length; i++)
        {
            total = total + chars[i];
        }
        return "[" + total + "]";
    }

//  [ ^ abc] Any character except a , b , or c( negation )

    public static String NOT_IN_SET(char[] chars)
    {
        String total = new String();
        for (int i = 0; i < chars.length; i++)
        {
            total = total + chars[i];
        }
        return "[^" + total + "]";
    }

    //   [a - zA - Z] a through z or A through Z , inclusive( range )
    //   [a - d[m - p]] a through d , or m through p : [a - dm - p] ( union )
    //   [a - z && [def]] d , e , or f( intersection )
    //   [a - z && [ ^ bc]] a through z , except for b and c :
    //   [ad - z] ( subtraction )
    //   [a - z && [ ^ m - p]] a through z ,and not m through p : [a - lq - z] ( subtraction )

//   Predefined character classes
    public static String DIGITS = "\\d"; // A digit : [0 - 9]
    public static String NON_DIGITS = "\\D"; //A non - digit : [ ^ 0 - 9]
    public static String WHITESPACE = "\\s"; // A whitespace character : [\t\n\x0B\f\r]
    public static String NON_WHITESPACE = "\\S"; // A non - whitespace character : [^\s]
    public static String WORD = "\\w"; // A word character : [a - zA - Z_0 - 9]
    public static String NON_WORD = "\\W"; // A non - word character : [^\w]

//   Boundary matchers
    public static String WORD_MIDDLE = "\\B"; // Match in the middle of a word
    public static String NON_WORD_MIDDLE = "\\b"; //  Match at the beginning or end of a word
    public static String WORD_BEGIN = "\\<"; //  Match at the beginning of a word
    public static String WORD_END = "\\>"; // Match at the end of a word
    public static String LINE_BEGIN = "^"; // Match at the beginning of a line
    public static String LINE_END = "$"; //Match at the end of a line
    public static String INPUT_BEGIN = "\\A"; // The beginning of the input
    public static String INPUT_END = "\\z"; // The end of the input
    public static String LAST_END = "\\G"; // The end of the previous match
    public static String INPUT_END_FINAL = "\\Z"; // The end of the input but for the final terminator , if any

//  Greedy quantifiers
    public static String ANY_ONE = "."; // Match any one character
    public static String ONE_OR_ZERO = "?"; // Match any character one time, if it exists
    public static String ZERO_OR_MORE = "*"; // Match declared element multiple times, if it exists
    public static String ONE_OR_MORE = "+"; // Match declared element one or more times
    public static String N_TIME(int n) // Match declared element exactly n times
    {
        return "{" + n + "}";
    }

    public static String N_TIME_OR_MORE(int n) // Match declared element at least n times
    {
        return "{" + n + ",}";
    }

    public static String N_TO_M(int n, int m) // Match declared element at least n but not more than m times
    {
        return "{" + n + "," + m + "}";
    }

    // type can be N_TO_M(),N_TIME_OR_MORE(),N_TIME()
    public static String RepeatString(String patternStr, String type)
    {
        return GROUP(patternStr) + type;
    }

    // type can be N_TO_M(),N_TIME_OR_MORE(),N_TIME(),ZERO_OR_MORE,ONE_OR_MORE
    public static String RepeatChar(char patternStr, String type)
    {
        return patternStr + type;
    }

//                Logical operators
    public static String FOLLOWEDBY = ""; //XY X followed by Y
    public static String OR = "|"; //    X | Y Either X or Y
    public static String GROUP(String X) //( X ) X , as a capturing group
    {
        return "(" + X + ")";
    }

    public static String GROUP_NONCAPTURING(String X) //( X ) X , as a capturing group
    {
        return "(?:" + X + ")";
    }

//                Back references
    public static String NTH_GROUP(int n) //\n Whatever the nth capturing group matched
    {
        return "\\" + n;
    }

    //               Quotation
    public static String QUOTE = "\\"; // Nothing , but quotes the following character
    public static String QUOTE_BEGIN = "\\Q"; // Nothing , but quotes all characters until \E
    public static String QUOTE_END = "\\E"; // Nothing , but ends quoting started by \Q

    //               Special constructs( non - capturing )
    /*                ( ? : X ) X , as a non - capturing group
         ( ? idmsux - idmsux ) Nothing , but turns match flags on - off
                    ( ? idmsux - idmsux : X ) X ,
                    as a non - capturing group with the given flags on - off
                    ( ? = X ) X , via zero - width positive lookahead
                    ( ? !X ) X , via zero - width negative lookahead
                    ( ? <= X ) X , via zero - width positive lookbehind
                    ( ? < !X ) X , via zero - width negative lookbehind
                    ( ? > X ) X , as an independent , non - capturing group
     */

    //flags
    // In this mode, only the '\n' line terminator is recognized in the
    // behavior of ., ^, and $.
    // Should be at the begining of a group
    public static String UNIX_LINES = "(?d)"; // Enables Unix lines mode.

    //Enables case-insensitive matching. By default, case-insensitive
    //matching assumes that only characters in the US-ASCII charset are
    //being matched. Unicode-aware case-insensitive matching can be enabled
    // by specifying the UNICODE_CASE flag in conjunction with this flag.
    // Should be at the begining of a group
    public static String CASE_INSENSITIVE = "(?i)";
    public static String CASE_SENSITIVE = "(?-i)";

    //Permits whitespace and comments in pattern. In this mode, whitespace is
    //ignored, and embedded comments starting with # are ignored until the end
    //of a line.
    // Should be at the begining of a group
    public static String COMMENTS = "(?x)";

    //Enables multiline mode. In multiline mode the expressions ^ and $
    //match just after or just before, respectively, a line terminator or
    //the end of the input sequence. By default these expressions only match
    // at the beginning and the end of the entire input sequence.
    // Should be at the begining of a group
    public static String MULTILINE = "(?m)";
    public static String SINGLELINE = "(?-m)";

    //Enables dotall mode. In dotall mode, the expression . matches any
    //character, including a line terminator. By default this expression
    //does not match line terminators.
    // Should be at the begining of a group
    public static String DOTALL = "(?s)";

    // Enables Unicode-aware case folding. When this flag is specified then
    // case-insensitive matching, when enabled by the CASE_INSENSITIVE flag,
    // is done in a manner consistent with the Unicode Standard. By default,
    //case-insensitive matching assumes that only characters in the US-ASCII
    // charset are being matched.
    // Should be at the begining of a group
    public static String UNICODE_CASE = "(?u)";

    //Enables canonical equivalence. When this flag is specified then two
    //characters will be considered to match if, and only if, their full
    //canonical decompositions match. The expression "a\u030A", for example,
    //will match the string "?" when this flag is specified. By default,
    //matching does not take canonical equivalence into account. There is no
    //embedded flag character for enabling canonical equivalence.
    //public static String CANON_EQ

    //======================== advance tokens
    // all contiguous whitespace characters
    // space. Line terminators are treated like whitespace.
    public static String BLANKS = "\\s+";

    // any character at any length
    public static String ANY_WORD = ".+";

}
