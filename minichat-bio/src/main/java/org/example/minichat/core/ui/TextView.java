package org.example.minichat.core.ui;

public class TextView implements View {

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    protected int rows = 50;
    protected int cols = 50;
    protected String[] lines;
    protected int textDirection = LEFT;
    protected boolean borderWrapText = false;

    public TextView() {
    }

    public TextView(String... lines) {
        this.lines = lines;
    }

    // private final Border border = new RoundedBorder();
    private Border border = new AngleBorder();

    public void setBorder(Border border) {
        this.border = border;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public void setTextDirection(int textDirection) {
        this.textDirection = textDirection;
    }

    public void setBorderWrapText(boolean borderWrapText) {
        this.borderWrapText = borderWrapText;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    private String formatText(String text) {
        // 出去两个边框占用的列还有边框和内容之间占用一个空格
        int availableCols = cols - 4;
        String formattedText = " ";
        if (text.length() > availableCols) {
            formattedText += text.substring(0, availableCols - 3) + "...";
        } else {
            formattedText += text;
        }
        return formattedText + " ";
    }

    private int findMaxTextLen() {
        int maxLen = 0;
        for (String line : lines) {
            String text = formatText(line);
            if (text.length() > maxLen) {
                maxLen = text.length();
                break;
            }
        }
        return maxLen;
    }

    public String doRender() {
        StringBuilder sb = new StringBuilder();
        Border invisibleBorder = new Border() {
            @Override
            public char[] getBorderCharacters() {
                return new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
            }
        };
        if (border == null) border = invisibleBorder;

        /*
            渲染内容部分
            如果内容的长度大于最大的行数，则只显示内容的后最大行数
         */
        int contentLines = Math.min(lines.length, rows - 2);
        int maxTextLen = findMaxTextLen();
        // 渲染头部
        renderLine(sb, border.getTopLeft(), border.getTop().repeat(borderWrapText ? maxTextLen : cols - 2), border.getTopRight(), maxTextLen);
        for (int i = lines.length - contentLines; i < lines.length; i++) {
            renderLine(sb, border.getLeft(), formatText(lines[i]), border.getRight(), maxTextLen);
        }
        // 渲染尾部
        renderLine(sb, border.getDownLeft(), border.getDown().repeat(borderWrapText ? maxTextLen : cols - 2), border.getDownRight(), maxTextLen);

        return sb.toString();
    }

    private void renderLine(StringBuilder sb, String left, String content, String right, int maxTextLen) {
        int availableCols = cols - content.length() - 2;

        int outerLeftCols = 0;
        int outerRightCols = 0;
        int leftCols = 0;
        int rightCols = 0;
        if (textDirection == 0) {
            rightCols = availableCols;
            if (borderWrapText) {
                outerRightCols = rightCols + content.length() - maxTextLen;
                rightCols -= outerRightCols;
            }
        } else if (textDirection == 1) {
            leftCols += availableCols / 2;
            rightCols = leftCols + (availableCols % 2);
            if (borderWrapText) {
                if (content.length() == maxTextLen) {
                    outerRightCols = rightCols;
                    outerLeftCols = leftCols;
                    leftCols = 0;
                    rightCols = 0;
                } else {
                    // System.out.printf("left: %d, right: %d, contentLen: %d availableCols: %d, maxLen: %d\n", leftCols, rightCols, content.length(), availableCols, maxTextLen);
                    int paddingLen = maxTextLen - content.length();
                    outerLeftCols = leftCols;
                    outerRightCols = rightCols;
                    leftCols = paddingLen / 2;
                    rightCols = leftCols + (paddingLen % 2);
                    // 特殊情况，最长文本是长度是奇数，短文本长度是偶数，需要交换
                    if (maxTextLen % 2 == 1 && content.length() % 2 == 0) {
                        int tmp = leftCols;
                        leftCols = rightCols;
                        rightCols = tmp;
                    }
                    outerLeftCols -= leftCols;
                    outerRightCols -= rightCols;
                    // System.out.printf("left: %d, right: %d, contentLen: %d availableCols: %d, maxLen: %d\n", leftCols, rightCols, content.length(), availableCols, maxTextLen);
                }
            }
        } else if (textDirection == 2) {
            leftCols = availableCols;
            if (borderWrapText) {
                outerLeftCols = leftCols + content.length() - maxTextLen;
                leftCols -= outerLeftCols;
            }
        }

        appendBlank(sb, outerLeftCols);
        sb.append(left);
        appendBlank(sb, leftCols);
        sb.append(content);
        appendBlank(sb, rightCols);
        sb.append(right);
        appendBlank(sb, outerRightCols);

        sb.append("\n");
    }

    private void appendBlank(StringBuilder sb, int count) {
        for (int j = 0; j < count; j++, sb.append(" ")) ;
    }

    public void render() {
        System.out.println(doRender());
    }

}
