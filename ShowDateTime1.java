/*
  Show Date Time #1 - Show Current Date or Time in Window
  Written by: Keith Fenske, http://kwfenske.github.io/
  Tuesday, 23 March 2010
  Java class name: ShowDateTime1
  Copyright (c) 2010 by Keith Fenske.  Apache License or GNU GPL.

  This is a Java 1.4 graphical (GUI) application to display the current date
  and/or time in a format and location of the user's choice.  Options are given
  on the command line, and can not be changed while the program is running.
  The program runs until you close its window, press the Escape key, or click
  the mouse on its display and choose Exit from the pop-up menu.

  The code that updates the date and time has less than twenty lines, because
  most real work is done by the Java JLabel and SimpleDateFormat classes.  Many
  more lines parse the options.  You almost have to be a Java programmer to
  understand these options or how to format the date and time.  Here is a
  command-line summary, raw and unedited:

      -? = -help = show summary of command-line syntax
      -b0 = hide window borders and controls; use full screen if -x1 given
      -b1 = -b = show borders and controls on application window (default)
      -d# = date and/or time format; see Java SimpleDateFormat description
      -f# = text font name; example: -fVerdana
      -p(#,#,#) = panel color or background in RGB; white is -p(255,255,255)
      -s# = text font size from 10 to 999 points; example: -s24
      -t(#,#,#) = text color or foreground in RGB; black is -t(0,0,0)
      -w(#,#,#,#) = normal window position: left, top, width, height;
          example: -w(50,50,700,500)
      -x0 = normal or regular window, don't maximize (default)
      -x1 = -x = maximize application window; full screen if -b0 given

  Options containing spaces or punctuation may need to be quoted according to
  your system's command syntax.  Information about the Java 5.0
  SimpleDateFormat class can be obtained from the following on-line reference:

      http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html

  At least, that's where the page was in 2010.  A more recent Java 8 page can
  be found at:

      http://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html

  Or search for "Java SimpleDateFormat class" with your favorite internet
  search engine.  The default date and time format is equivalent to the
  following quoted command on Windows:

      java  ShowDateTime1  "-d'<html>'EEE d MMM yyyy'<br>'h:mm:ss a z'</html>'"

  Note the <html> and <br> tags to produce multiple lines.  It would take
  another two pages here to summarize the syntax, so please go read the web
  page instead.

  Apache License or GNU General Public License
  --------------------------------------------
  ShowDateTime1 is free software and has been released under the terms and
  conditions of the Apache License (version 2.0 or later) and/or the GNU
  General Public License (GPL, version 2 or later).  This program is
  distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the license(s) for more details.  You should have
  received a copy of the licenses along with this program.  If not, see the
  http://www.apache.org/licenses/ and http://www.gnu.org/licenses/ web pages.
*/

import java.awt.*;                // older Java GUI support
import java.awt.event.*;          // older Java GUI event support
import java.text.*;               // number formatting
import java.util.*;               // calendars, dates, lists, maps, vectors
import java.util.regex.*;         // regular expressions
import javax.swing.*;             // newer Java GUI support

public class ShowDateTime1
{
  /* constants */

  static final String COPYRIGHT_NOTICE =
    "Copyright (c) 2010 by Keith Fenske.  Apache License or GNU GPL.";
  static final int MIN_FRAME = 50; // minimum window height or width in pixels
  static final String PROGRAM_TITLE =
    "Show Current Date or Time in Window - by: Keith Fenske";
  static final int TIMER_DELAY = 100; // 0.100 seconds between status updates

  /* class variables */

  static SimpleDateFormat formatDate; // formats date/time as numeric text
  static JMenuItem menuCancel, menuExit; // menu items for <menuPopup>
  static JPopupMenu menuPopup;    // pop-up menu invoked by any mouse click
  static String oldText;          // previous string for date/time text
  static JLabel outputText;       // text area where we put date and/or time
  static javax.swing.Timer updateTimer; // timer for updating output text area

/*
  main() method

  We run as a graphical application only.  Set the window layout and then let
  the graphical interface run the show.
*/
  public static void main(String[] args)
  {
    ActionListener action;        // our shared action listener
    boolean borderFlag;           // true if main window has borders, controls
    String fontName;              // font name for text in output text area
    int fontSize;                 // point size for text in output text area
    int i;                        // index variable
    JFrame mainFrame;             // this application's window if GUI
    boolean maximizeFlag;         // true if we maximize our main window
    boolean mswinFlag;            // true if running on Microsoft Windows
    Color panelColor, textColor;  // background and foreground colors
    int windowHeight, windowLeft, windowTop, windowWidth;
                                  // position and size for <mainFrame>
    String word;                  // one parameter from command line

    /* Initialize variables used by both console and GUI applications. */

    borderFlag = true;            // by default, window has borders, controls
    fontName = "Verdana";         // preferred font name for output text area
    fontSize = 36;                // default point size for output text area
    formatDate = new SimpleDateFormat(
      "'<html>'EEE d MMM yyyy'<br>'h:mm:ss a z'</html>'"); // date/time format
    maximizeFlag = false;         // by default, don't maximize our main window
    mswinFlag = System.getProperty("os.name").startsWith("Windows");
    oldText = null;               // no previous string for date/time text
    panelColor = new Color(224, 224, 255); // default background color
    textColor = new Color(51, 51, 51); // default text color
    windowHeight = 150;           // default window position and size
    windowLeft = 100;
    windowTop = 100;
    windowWidth = 400;

    /* Check command-line parameters for options. */

    for (i = 0; i < args.length; i ++)
    {
      word = args[i].toLowerCase(); // easier to process if consistent case
      if (word.length() == 0)
      {
        /* Ignore empty parameters, which are more common than you might think,
        when programs are being run from inside scripts (command files). */
      }

      else if (word.equals("?") || word.equals("-?") || word.equals("/?")
        || word.equals("-h") || (mswinFlag && word.equals("/h"))
        || word.equals("-help") || (mswinFlag && word.equals("/help")))
      {
        showHelp();               // show help summary
        System.exit(0);           // exit application after printing help
      }

      else if (word.equals("-b") || (mswinFlag && word.equals("/b"))
        || word.equals("-b1") || (mswinFlag && word.equals("/b1")))
      {
        borderFlag = true;        // our main window has borders, controls
      }
      else if (word.equals("-b0") || (mswinFlag && word.equals("/b0")))
        borderFlag = false;       // no borders, controls on main window

      else if (word.startsWith("-d") || (mswinFlag && word.startsWith("/d")))
        formatDate = new SimpleDateFormat(args[i].substring(2));
                                  // accept anything for date/time format

      else if (word.startsWith("-f") || (mswinFlag && word.startsWith("/f")))
        fontName = args[i].substring(2); // accept anything for font name

      else if (word.startsWith("-p") || (mswinFlag && word.startsWith("/p")))
      {
        /* This option is followed by a panel color or background in RGB. */

        int blue, green, red;     // local variables for color values
        blue = green = red = -1;  // default color values are invalid
        Pattern pattern = Pattern.compile(
          "\\s*\\(\\s*(\\d{1,4})\\s*,\\s*(\\d{1,4})\\s*,\\s*(\\d{1,4})\\s*\\)\\s*");
        Matcher matcher = pattern.matcher(word.substring(2)); // parse option
        if (matcher.matches())    // if option has proper syntax
        {
          red = Integer.parseInt(matcher.group(1));
          green = Integer.parseInt(matcher.group(2));
          blue = Integer.parseInt(matcher.group(3));
        }
        else                      // bad syntax or too many digits
        {
          blue = green = red = -1; // mark result as invalid
        }
        if ((blue < 0) || (blue > 255) || (green < 0) || (green > 255) ||
          (red < 0) || (red > 255))
        {
          System.err.println("Invalid background color: " + args[i]);
          showHelp();             // show help summary
          System.exit(-1);        // exit application after printing help
        }
        panelColor = new Color(red, green, blue); // use this background color
      }

      else if (word.startsWith("-s") || (mswinFlag && word.startsWith("/s")))
      {
        /* This option is followed by a font size for the output text area. */

        int size = -1;            // default value for font point size
        try                       // try to parse remainder as unsigned integer
        {
          size = Integer.parseInt(word.substring(2));
        }
        catch (NumberFormatException nfe) // if not a number or bad syntax
        {
          size = -1;              // set result to an illegal value
        }
        if ((size < 10) || (size > 999))
        {
          System.err.println("Invalid font point size " + args[i]);
          showHelp();             // show help summary
          System.exit(-1);        // exit application after printing help
        }
        fontSize = size;          // use this point size for output text area
      }

      else if (word.startsWith("-t") || (mswinFlag && word.startsWith("/t")))
      {
        /* This option is followed by a text color or foreground in RGB. */

        int blue, green, red;     // local variables for color values
        blue = green = red = -1;  // default color values are invalid
        Pattern pattern = Pattern.compile(
          "\\s*\\(\\s*(\\d{1,4})\\s*,\\s*(\\d{1,4})\\s*,\\s*(\\d{1,4})\\s*\\)\\s*");
        Matcher matcher = pattern.matcher(word.substring(2)); // parse option
        if (matcher.matches())    // if option has proper syntax
        {
          red = Integer.parseInt(matcher.group(1));
          green = Integer.parseInt(matcher.group(2));
          blue = Integer.parseInt(matcher.group(3));
        }
        else                      // bad syntax or too many digits
        {
          blue = green = red = -1; // mark result as invalid
        }
        if ((blue < 0) || (blue > 255) || (green < 0) || (green > 255) ||
          (red < 0) || (red > 255))
        {
          System.err.println("Invalid foreground color: " + args[i]);
          showHelp();             // show help summary
          System.exit(-1);        // exit application after printing help
        }
        textColor = new Color(red, green, blue); // use this foreground color
      }

      else if (word.startsWith("-w") || (mswinFlag && word.startsWith("/w")))
      {
        /* This option is followed by a list of four numbers for the initial
        window position and size. */

        Pattern pattern = Pattern.compile(
          "\\s*\\(\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*,\\s*(\\d{1,5})\\s*\\)\\s*");
        Matcher matcher = pattern.matcher(word.substring(2)); // parse option
        if (matcher.matches())    // if option has proper syntax
        {
          windowLeft = Integer.parseInt(matcher.group(1));
          windowTop = Integer.parseInt(matcher.group(2));
          windowWidth = Integer.parseInt(matcher.group(3));
          windowHeight = Integer.parseInt(matcher.group(4));
        }
        else                      // bad syntax or too many digits
        {
          windowHeight = windowLeft = windowTop = windowWidth = -1;
                                  // mark result as invalid
        }
        if ((windowHeight < MIN_FRAME) || (windowWidth < MIN_FRAME))
        {
          System.err.println("Invalid window position or size: " + args[i]);
          showHelp();             // show help summary
          System.exit(-1);        // exit application after printing help
        }
      }

      else if (word.equals("-x") || (mswinFlag && word.equals("/x"))
        || word.equals("-x1") || (mswinFlag && word.equals("/x1")))
      {
        maximizeFlag = true;      // maximize our main window
      }
      else if (word.equals("-x0") || (mswinFlag && word.equals("/x0")))
        maximizeFlag = false;     // regular window, don't maximize

      else
      {
        System.err.println("Option not recognized: " + args[i]);
        showHelp();               // show help summary
        System.exit(-1);          // exit application after printing help
      }
    }

    /* Initialize shared graphical objects. */

    action = new ShowDateTime1User(); // create our shared action listener

    /* This is a pop-up menu invoked by any mouse click on the text area. */

    menuPopup = new JPopupMenu();

    menuCancel = new JMenuItem("Cancel"); // ignore the mouse click
    menuCancel.addActionListener(action);
    menuPopup.add(menuCancel);

    menuExit = new JMenuItem("Exit"); // exit from this application
    menuExit.addActionListener(action);
    menuPopup.add(menuExit);

    /* Output text area for the date and/or time display.  We use a simple
    JLabel to center the text both horizontally and vertically. */

    oldText = formatDate.format(new Date()); // format and save text
    outputText = new JLabel(oldText, JLabel.CENTER);
    outputText.addKeyListener((KeyListener) action);
    outputText.addMouseListener((MouseListener) action);
    outputText.setBackground(panelColor);
    outputText.setFocusable(true); // accept keyboard characters
    outputText.setFont(new Font(fontName, Font.PLAIN, fontSize));
    outputText.setForeground(textColor);
    outputText.setOpaque(true);   // use our choice for background color

    /* The main application window.  Use setUndecorated(true) to remove borders
    and window controls, and to use the full screen for a maximized window. */

    mainFrame = new JFrame("Date Time Zone");
    mainFrame.getContentPane().add(outputText, BorderLayout.CENTER);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setLocation(windowLeft, windowTop); // normal top-left corner
    mainFrame.setSize(windowWidth, windowHeight); // size of normal window
    mainFrame.setUndecorated(! borderFlag); // window borders and controls
    if (maximizeFlag) mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mainFrame.validate();         // recheck application window layout
    mainFrame.setVisible(true);   // and then show application window

    /* Let the graphical interface run the application now. */

    outputText.requestFocusInWindow(); // give keyboard focus to output text
    updateTimer = new javax.swing.Timer(TIMER_DELAY, action);
    updateTimer.start();          // update output text on clock ticks only

  } // end of main() method

// ------------------------------------------------------------------------- //

/*
  showHelp() method

  Show the help summary.  This is a UNIX standard and is expected for all
  console applications, even very simple ones.
*/
  static void showHelp()
  {
    System.err.println();
    System.err.println(PROGRAM_TITLE);
    System.err.println();
    System.err.println("This is a graphical application.  You may give options on the command line:");
    System.err.println();
    System.err.println("  -? = -help = show summary of command-line syntax");
    System.err.println("  -b0 = hide window borders and controls; use full screen if -x1 given");
    System.err.println("  -b1 = -b = show borders and controls on application window (default)");
    System.err.println("  -d# = date and/or time format; see Java SimpleDateFormat description");
    System.err.println("  -f# = text font name; example: -fVerdana");
    System.err.println("  -p(#,#,#) = panel color or background in RGB; white is -p(255,255,255)");
    System.err.println("  -s# = text font size from 10 to 999 points; example: -s24");
    System.err.println("  -t(#,#,#) = text color or foreground in RGB; black is -t(0,0,0)");
    System.err.println("  -w(#,#,#,#) = normal window position: left, top, width, height;");
    System.err.println("      example: -w(50,50,700,500)");
    System.err.println("  -x0 = normal or regular window, don't maximize (default)");
    System.err.println("  -x1 = -x = maximize application window; full screen if -b0 given");
    System.err.println();
    System.err.println("Options containing spaces or punctuation may need to be quoted according to");
    System.err.println("your system's command syntax.");
    System.err.println();
    System.err.println(COPYRIGHT_NOTICE);
//  System.err.println();

  } // end of showHelp() method

/*
  userButton() method

  This method is called by our action listener actionPerformed() to process
  buttons, in the context of the main ShowDateTime1 class.
*/
  static void userButton(ActionEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == menuCancel)     // "Cancel" item on mouse pop-up menu
    {
      /* The pop-up menu will disappear by itself if we ignore this event. */
    }
    else if (source == menuExit)  // "Exit" item on mouse pop-up menu
    {
      System.exit(0);             // always exit with zero status from GUI
    }
    else if (source == updateTimer) // update timer for output text area
    {
      String newText = formatDate.format(new Date()); // format date and time
      if (newText.equals(oldText) == false) // has date or time changed?
      {
        outputText.setText(newText); // yes, show new date or time
        oldText = newText;        // next time, compare against this text
      }
    }
    else                          // fault in program logic, not by user
    {
      System.err.println("Error in userButton(): unknown ActionEvent: "
        + event);                 // should never happen, so write on console
    }
  } // end of userButton() method

} // end of ShowDateTime1 class

// ------------------------------------------------------------------------- //

/*
  ShowDateTime1User class

  This class listens to input from the user and passes back event parameters to
  a static method in the main class.
*/

class ShowDateTime1User implements ActionListener, KeyListener, MouseListener
{
  /* empty constructor */

  public ShowDateTime1User() { }

  /* button listener, dialog boxes, menu items, etc */

  public void actionPerformed(ActionEvent event)
  {
    ShowDateTime1.userButton(event);
  }

  /* keyboard listener: special key to exit application */

  public void keyPressed(KeyEvent event) { /* not used */ }
  public void keyReleased(KeyEvent event)
  {
    if (event.getKeyCode() == KeyEvent.VK_ESCAPE) // only the Escape key
      System.exit(0);             // always exit with zero status from GUI
  }
  public void keyTyped(KeyEvent event) { /* not used */ }

  /* mouse listener: always the cancel/exit pop-up menu */

  public void mouseClicked(MouseEvent event)
  {
    ShowDateTime1.menuPopup.show(event.getComponent(), event.getX(),
      event.getY());
  }
  public void mouseEntered(MouseEvent event) { /* not used */ }
  public void mouseExited(MouseEvent event) { /* not used */ }
  public void mousePressed(MouseEvent event) { /* not used */ }
  public void mouseReleased(MouseEvent event) { /* not used */ }

} // end of ShowDateTime1User class

/* Copyright (c) 2010 by Keith Fenske.  Apache License or GNU GPL. */
