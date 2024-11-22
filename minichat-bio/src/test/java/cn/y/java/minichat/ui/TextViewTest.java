package cn.y.java.minichat.ui;

import cn.y.java.minichat.core.ui.TextView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TextViewTest {

    private final int cols = 20;

    @ParameterizedTest
    @CsvSource({
            "this is text, 0, false, '┌──────────────────┐\n│ this is text     │\n└──────────────────┘\n'",
            "this is text, 1, false, '┌──────────────────┐\n│   this is text   │\n└──────────────────┘\n'",
            "this is text, 2, false, '┌──────────────────┐\n│     this is text │\n└──────────────────┘\n'",
            "this is txt, 0, false, '┌──────────────────┐\n│ this is txt      │\n└──────────────────┘\n'",
            "this is txt, 1, false, '┌──────────────────┐\n│   this is txt    │\n└──────────────────┘\n'",
            "this is txt, 2, false, '┌──────────────────┐\n│      this is txt │\n└──────────────────┘\n'",
            "this is text, 0, true, '┌──────────────┐    \n│ this is text │    \n└──────────────┘    \n'",
            "this is text, 1, true, '  ┌──────────────┐  \n  │ this is text │  \n  └──────────────┘  \n'",
            "this is text, 2, true, '    ┌──────────────┐\n    │ this is text │\n    └──────────────┘\n'",
            "this is txt, 0, true, '┌─────────────┐     \n│ this is txt │     \n└─────────────┘     \n'",
            "this is txt, 1, true, '  ┌─────────────┐   \n  │ this is txt │   \n  └─────────────┘   \n'",
            "this is txt, 2, true, '     ┌─────────────┐\n     │ this is txt │\n     └─────────────┘\n'",
            "this is looooooooooooooooog text, 0, false, '┌──────────────────┐\n│ this is loooo... │\n└──────────────────┘\n'",
    })
    public void testRenderTextView(String text, int textDirection, boolean wrapText, String excepted) {
        TextView textView = new TextView(text);
        textView.setCols(cols);
        textView.setTextDirection(textDirection);
        textView.setBorderWrapText(wrapText);
        System.out.println(excepted);
        Assertions.assertEquals(excepted, textView.doRender());
    }

    @ParameterizedTest
    @CsvSource({
            "this is text, abc, 0, false, '┌──────────────────┐\n│ this is text     │\n│ abc              │\n└──────────────────┘\n'",
            "this is text, abc, 1, false, '┌──────────────────┐\n│   this is text   │\n│       abc        │\n└──────────────────┘\n'",
            "this is text, abc, 2, false, '┌──────────────────┐\n│     this is text │\n│              abc │\n└──────────────────┘\n'",
            "this is text, abcd, 0, false, '┌──────────────────┐\n│ this is text     │\n│ abcd             │\n└──────────────────┘\n'",
            "this is text, abcd, 1, false, '┌──────────────────┐\n│   this is text   │\n│       abcd       │\n└──────────────────┘\n'",
            "this is text, abcd, 2, false, '┌──────────────────┐\n│     this is text │\n│             abcd │\n└──────────────────┘\n'",
            "this is txt, abc, 0, false, '┌──────────────────┐\n│ this is txt      │\n│ abc              │\n└──────────────────┘\n'",
            "this is txt, abc, 1, false, '┌──────────────────┐\n│   this is txt    │\n│       abc        │\n└──────────────────┘\n'",
            "this is txt, abc, 2, false, '┌──────────────────┐\n│      this is txt │\n│              abc │\n└──────────────────┘\n'",
            "this is txt, abcd, 0, false, '┌──────────────────┐\n│ this is txt      │\n│ abcd             │\n└──────────────────┘\n'",
            "this is txt, abcd, 1, false, '┌──────────────────┐\n│   this is txt    │\n│       abcd       │\n└──────────────────┘\n'",
            "this is txt, abcd, 2, false, '┌──────────────────┐\n│      this is txt │\n│             abcd │\n└──────────────────┘\n'",
            "this is text, abc, 0, true, '┌──────────────┐    \n│ this is text │    \n│ abc          │    \n└──────────────┘    \n'",
            "this is text, abc, 1, true, '  ┌──────────────┐  \n  │ this is text │  \n  │     abc      │  \n  └──────────────┘  \n'",
            "this is text, abc, 2, true, '    ┌──────────────┐\n    │ this is text │\n    │          abc │\n    └──────────────┘\n'",
            "this is text, abcd, 0, true, '┌──────────────┐    \n│ this is text │    \n│ abcd         │    \n└──────────────┘    \n'",
            "this is text, abcd, 1, true, '  ┌──────────────┐  \n  │ this is text │  \n  │     abcd     │  \n  └──────────────┘  \n'",
            "this is text, abcd, 2, true, '    ┌──────────────┐\n    │ this is text │\n    │         abcd │\n    └──────────────┘\n'",
            "this is txt, abc, 0, true, '┌─────────────┐     \n│ this is txt │     \n│ abc         │     \n└─────────────┘     \n'",
            "this is txt, abc, 1, true, '  ┌─────────────┐   \n  │ this is txt │   \n  │     abc     │   \n  └─────────────┘   \n'",
            "this is txt, abc, 2, true, '     ┌─────────────┐\n     │ this is txt │\n     │         abc │\n     └─────────────┘\n'",
            "this is txt, abcd, 0, true, '┌─────────────┐     \n│ this is txt │     \n│ abcd        │     \n└─────────────┘     \n'",
            "this is txt, abcd, 1, true, '  ┌─────────────┐   \n  │ this is txt │   \n  │     abcd    │   \n  └─────────────┘   \n'",
            "this is txt, abcd, 2, true, '     ┌─────────────┐\n     │ this is txt │\n     │        abcd │\n     └─────────────┘\n'",
            "this is looooooooooooooooog text, abc, 0, false, '┌──────────────────┐\n│ this is loooo... │\n│ abc              │\n└──────────────────┘\n'",
            "this is looooooooooooooooog text, abcd, 0, false, '┌──────────────────┐\n│ this is loooo... │\n│ abcd             │\n└──────────────────┘\n'",
    })
    public void testRenderTextViewMutLines(String text, String text1, int textDirection, boolean wrapText, String excepted) {
        TextView textView = new TextView(text, text1);
        textView.setCols(cols);
        textView.setTextDirection(textDirection);
        textView.setBorderWrapText(wrapText);
        System.out.println(excepted);
        Assertions.assertEquals(excepted, textView.doRender());
    }
}
