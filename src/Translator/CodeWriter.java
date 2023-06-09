package Translator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CodeWriter {

    public static final String DEST_FIX = ".asm";
    public static final String SOURCE_FIX = ".vm";
    public static final String SP = "SP";
    public static final String SEGMENT_LCL = "LCL";
    public static final String SEGMENT_ARG = "ARG";
    public static final String SEGMENT_THIS = "THIS";
    public static final String SEGMENT_THAT = "THAT";
    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");
    public static final String LABEL_PATTEN1 = "{0}${1}";
    public static final String LABEL_PATTEN2 = "{0}.{1}";
    public static final String LABEL_PATTEN3 = "{0}.{1}.{2}";
    public static final String L_BEGIN_TAG = "(";
    public static final String L_END_TAG = ")";
    public static final String A_TAG = "@";
    public static final String COMMENT_TAG = "//";

    private String filePath;
    private String asmName;
    private FileWriter fWriter;
    private static int seq = -1;
    private String curVMFileName;
    public void setAsmName(final String asmStr) {
        this.asmName = asmStr;
    }
    public void setFilePath(final String filePath) {
        this.filePath = filePath.endsWith(File.separator) ? filePath : filePath
                + File.separator;

    }
    public void setFileName(final String filename) {
        String asmFileStr = (asmName == null) ? filename : asmName;
        if (fWriter == null) {
            try {
                File f = new File(filePath + asmFileStr + DEST_FIX);
                if (f.exists()) {
                    f.delete();
                }
                fWriter = new FileWriter(f);
                writeComment("	******* HACK ASM FILE GENERATED BY VM COMPILER *******");
                writeComment("	VM  FILE: " + asmFileStr + SOURCE_FIX);
                writeComment("	COMPILER: JAVA_HACK_VM_COMPILER");
                writeComment("	GEN TIME: "
                        + SimpleDateFormat.getInstance().format(new Date())
                        + LINE_SEPARATOR + LINE_SEPARATOR);
                writeInit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        curVMFileName = filename;
    }
    public void writeComment(final String contant) {
        try {
            fWriter.write(COMMENT_TAG + " " + contant + LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeArithmetic(final String command) {

        StringBuffer strB = new StringBuffer();
        String lab1, lab2, lab3, result;

        if ("add".equalsIgnoreCase(command) || "sub".equalsIgnoreCase(command)
                || "and".equalsIgnoreCase(command)
                || "or".equalsIgnoreCase(command)) {
            strB.append(A_TAG).append(SP).append(LINE_SEPARATOR)
                    .append("AM=M-1").append(LINE_SEPARATOR).append("D=M")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("AM=M-1")
                    .append(LINE_SEPARATOR).append("[wildcard]")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("M=M+1")
                    .append(LINE_SEPARATOR);
            result = strB.toString();
        } else if ("eq".equalsIgnoreCase(command)
                || "gt".equalsIgnoreCase(command)
                || "lt".equalsIgnoreCase(command)) {

            ++seq;
            lab1 = MessageFormat.format(LABEL_PATTEN3, new Object[] { "COMP",
                    seq, "TRUE" });
            lab2 = MessageFormat.format(LABEL_PATTEN3, new Object[] { "COMP",
                    seq, "FALSE" });
            lab3 = MessageFormat.format(LABEL_PATTEN3, new Object[] { "COMP",
                    seq, "END" });

            strB.append(A_TAG).append(SP).append(LINE_SEPARATOR)
                    .append("AM=M-1").append(LINE_SEPARATOR).append("D=M")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("AM=M-1")
                    .append(LINE_SEPARATOR).append("D=M-D")
                    .append(LINE_SEPARATOR).append(A_TAG).append(lab1)
                    .append(LINE_SEPARATOR).append("[wildcard]")
                    .append(LINE_SEPARATOR).append(A_TAG).append(lab2)
                    .append(LINE_SEPARATOR).append("0;JMP")
                    .append(LINE_SEPARATOR).append(L_BEGIN_TAG).append(lab1)
                    .append(L_END_TAG).append(LINE_SEPARATOR).append(A_TAG)
                    .append(SP).append(LINE_SEPARATOR).append("A=M")
                    .append(LINE_SEPARATOR).append("M=-1")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("M=M+1")
                    .append(LINE_SEPARATOR).append(A_TAG).append(lab3)
                    .append(LINE_SEPARATOR).append("0;JMP")
                    .append(LINE_SEPARATOR).append(L_BEGIN_TAG).append(lab2)
                    .append(L_END_TAG).append(LINE_SEPARATOR).append(A_TAG)
                    .append(SP).append(LINE_SEPARATOR).append("A=M")
                    .append(LINE_SEPARATOR).append("M=0")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("M=M+1")
                    .append(LINE_SEPARATOR).append(L_BEGIN_TAG).append(lab3)
                    .append(L_END_TAG).append(LINE_SEPARATOR);
        } else if ("neg".equalsIgnoreCase(command)
                || "not".equalsIgnoreCase(command)) {
            strB.append(A_TAG).append(SP).append(LINE_SEPARATOR)
                    .append("AM=M-1").append(LINE_SEPARATOR)
                    .append("[wildcard]").append(LINE_SEPARATOR).append(A_TAG)
                    .append(SP).append(LINE_SEPARATOR).append("M=M+1")
                    .append(LINE_SEPARATOR);

        }
        result = strB.toString();
        switch (Arrays.asList(Parser.TYPE_ARI_LOG).indexOf(command)) {
            case 0:
                result = result.replace("[wildcard]", "M=D+M");
                break;
            case 1:
                result = result.replace("[wildcard]", "M=M-D");
                break;
            case 2:
                result = result.replace("[wildcard]", "M=-M");
                break;
            case 3:
                result = result.replace("[wildcard]", "D;JEQ");
                break;
            case 4:
                result = result.replace("[wildcard]", "D;JGT");
                break;
            case 5:
                result = result.replace("[wildcard]", "D;JLT");
                break;
            case 6:
                result = result.replace("[wildcard]", "M=D&M");
                break;
            case 7:
                result = result.replace("[wildcard]", "M=D|M");
                break;
            default:
                result = result.replace("[wildcard]", "M=!M");
                break;
        }

        try {
            fWriter.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writePushPop(final String command, final String segment,
                             final int index) {
        StringBuffer strB = new StringBuffer();

        if (Parser.TYPE_PUSH.equalsIgnoreCase(command)) {
            if (segment.equalsIgnoreCase("constant")) {
                strB.append(A_TAG).append(index).append(LINE_SEPARATOR)
                        .append("D=A").append(LINE_SEPARATOR).append(A_TAG)
                        .append(SP).append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                        .append(LINE_SEPARATOR).append("M=M+1")
                        .append(LINE_SEPARATOR);
            }

            if (segment.equalsIgnoreCase("local")
                    || segment.equalsIgnoreCase("argument")
                    || segment.equalsIgnoreCase("this")
                    || segment.equalsIgnoreCase("that")) {

                String seg;
                if (segment.equalsIgnoreCase("local")) {
                    seg = SEGMENT_LCL;
                } else if (segment.equalsIgnoreCase("argument")) {
                    seg = SEGMENT_ARG;
                } else if (segment.equalsIgnoreCase("this")) {
                    seg = SEGMENT_THIS;
                } else {
                    seg = SEGMENT_THAT;
                }

                strB.append(A_TAG).append(index).append(LINE_SEPARATOR)
                        .append("D=A").append(LINE_SEPARATOR).append(A_TAG)
                        .append(seg).append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("D=D+A")
                        .append(LINE_SEPARATOR).append("A=D")
                        .append(LINE_SEPARATOR).append("D=M")
                        .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                        .append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                        .append(LINE_SEPARATOR).append("M=M+1")
                        .append(LINE_SEPARATOR);
            }

            if (segment.equalsIgnoreCase("temp")
                    || segment.equalsIgnoreCase("pointer")
                    || segment.equalsIgnoreCase("static")) {
                String var = MessageFormat.format(LABEL_PATTEN2, new Object[] {
                        curVMFileName, index });
                int base = 5;
                if (segment.equalsIgnoreCase("pointer")) {
                    base = 3;
                }

                String varStr = segment.equalsIgnoreCase("static") ? var : "R"
                        + (base + index);

                strB.append(A_TAG).append(varStr).append(LINE_SEPARATOR)
                        .append("D=M").append(LINE_SEPARATOR).append(A_TAG)
                        .append(SP).append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                        .append(LINE_SEPARATOR).append("M=M+1")
                        .append(LINE_SEPARATOR);
            }
        }

        if (Parser.TYPE_POP.equalsIgnoreCase(command)) {
            if (segment.equalsIgnoreCase("local")
                    || segment.equalsIgnoreCase("argument")
                    || segment.equalsIgnoreCase("this")
                    || segment.equalsIgnoreCase("that")) {

                String seg;
                if (segment.equalsIgnoreCase("local")) {
                    seg = SEGMENT_LCL;
                } else if (segment.equalsIgnoreCase("argument")) {
                    seg = SEGMENT_ARG;
                } else if (segment.equalsIgnoreCase("this")) {
                    seg = SEGMENT_THIS;
                } else {
                    seg = SEGMENT_THAT;
                }

                strB.append(A_TAG).append(index).append(LINE_SEPARATOR)
                        .append("D=A").append(LINE_SEPARATOR).append(A_TAG)
                        .append(seg).append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("D=D+A")
                        .append(LINE_SEPARATOR).append(A_TAG).append(seg)
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                        .append(LINE_SEPARATOR).append("AM=M-1")
                        .append(LINE_SEPARATOR).append("D=M")
                        .append(LINE_SEPARATOR).append(A_TAG).append(seg)
                        .append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(index)
                        .append(LINE_SEPARATOR).append("D=A")
                        .append(LINE_SEPARATOR).append(A_TAG).append(seg)
                        .append(LINE_SEPARATOR).append("A=M")
                        .append(LINE_SEPARATOR).append("D=A-D")
                        .append(LINE_SEPARATOR).append(A_TAG).append(seg)
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR);
            }

            if (segment.equalsIgnoreCase("temp")
                    || segment.equalsIgnoreCase("pointer")
                    || segment.equalsIgnoreCase("static")) {
                String var = MessageFormat.format(LABEL_PATTEN2, new Object[] {
                        curVMFileName, index });
                int base = 5;
                if (segment.equalsIgnoreCase("pointer")) {
                    base = 3;
                }

                String varStr = segment.equalsIgnoreCase("static") ? var : "R"
                        + (base + index);

                strB.append(A_TAG).append(SP).append(LINE_SEPARATOR)
                        .append("AM=M-1").append(LINE_SEPARATOR).append("D=M")
                        .append(LINE_SEPARATOR).append(A_TAG)
                        .append(varStr)
                        .append(LINE_SEPARATOR).append("M=D")
                        .append(LINE_SEPARATOR);
            }
        }
        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeInit() {
        StringBuffer strB = new StringBuffer();
        int spIndex = 0x100;
        strB.append(COMMENT_TAG).append(" bootstrap").append(LINE_SEPARATOR)
                .append(A_TAG).append(spIndex).append(LINE_SEPARATOR)
                .append("D=A").append(LINE_SEPARATOR).append(A_TAG).append(SP)
                .append(LINE_SEPARATOR).append("M=D").append(LINE_SEPARATOR);
        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeCall("Sys.init", 0);
    }
    public void writeLabel(final String label) {
        StringBuffer strB = new StringBuffer();
        strB.append(L_BEGIN_TAG).append(label).append(L_END_TAG)
                .append(LINE_SEPARATOR);
        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeGoto(final String label) {
        StringBuffer strB = new StringBuffer();
        strB.append(A_TAG).append(label).append(LINE_SEPARATOR).append("0;JMP")
                .append(LINE_SEPARATOR);

        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeIf(final String label) {
        StringBuffer strB = new StringBuffer();
        strB.append(A_TAG).append(SP).append(LINE_SEPARATOR).append("AM=M-1")
                .append(LINE_SEPARATOR).append("D=M").append(LINE_SEPARATOR)
                .append(A_TAG).append(label).append(LINE_SEPARATOR)
                .append("D;JNE").append(LINE_SEPARATOR);

        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCall(final String functionName, final int numArgs) {
        StringBuffer strB = new StringBuffer();
        String retLab = "RETURN" + (++seq);
        strB.append(A_TAG)
                .append(retLab)
                .append(LINE_SEPARATOR)
                .append("D=A")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M+1")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" push return-address")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_LCL)
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M+1")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" push LCL")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_ARG)
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M+1")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" push ARG")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_THIS)
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M+1")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" push THIS")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append(SEGMENT_THAT).append(LINE_SEPARATOR)
                .append("D=M").append(LINE_SEPARATOR).append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M+1")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" push THAT")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append(SP).append(LINE_SEPARATOR).append("D=M")
                .append(LINE_SEPARATOR).append(A_TAG).append(numArgs)
                .append(LINE_SEPARATOR).append("D=D-A").append(LINE_SEPARATOR)
                .append(A_TAG).append(5).append(LINE_SEPARATOR).append("D=D-A")
                .append(LINE_SEPARATOR).append(A_TAG).append(SEGMENT_ARG)
                .append(LINE_SEPARATOR).append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" ARG = SP-n-5")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append(SP).append(LINE_SEPARATOR).append("D=M")
                .append(LINE_SEPARATOR).append(A_TAG).append(SEGMENT_LCL)
                .append(LINE_SEPARATOR).append("M=D").append('\t')
                .append(COMMENT_TAG).append(" LCL = SP").append(LINE_SEPARATOR);

        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeGoto(functionName);
        writeLabel(retLab);
    }

    public void writeReturn() {
        StringBuffer strB = new StringBuffer();
        strB.append(A_TAG)
                .append(SEGMENT_LCL)
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append("frame")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" FRAME = LCL")
                .append(LINE_SEPARATOR)

                .append(A_TAG)
                .append(5)
                .append(LINE_SEPARATOR)
                .append("D=D-A")
                .append(LINE_SEPARATOR)
                .append("A=D")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append("ret")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" RET = *(FRAME-5)")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=M-1")
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_ARG)
                .append(LINE_SEPARATOR)
                .append("A=M")
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" *ARG = pop")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_ARG)
                .append(LINE_SEPARATOR)
                .append("D=M+1")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SP)
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" SP = ARG+1")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append("frame")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(1)
                .append(LINE_SEPARATOR)
                .append("D=D-A")
                .append(LINE_SEPARATOR)
                .append("A=D")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_THAT)
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" THAT = *(FRAME-1)")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append("frame")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(2)
                .append(LINE_SEPARATOR)
                .append("D=D-A")
                .append(LINE_SEPARATOR)
                .append("A=D")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_THIS)
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" THIS = *(FRAME-2)")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append("frame").append(LINE_SEPARATOR)
                .append("D=M").append(LINE_SEPARATOR).append(A_TAG).append(3)
                .append(LINE_SEPARATOR)
                .append("D=D-A")
                .append(LINE_SEPARATOR)
                .append("A=D")
                .append(LINE_SEPARATOR)
                .append("D=M")
                .append(LINE_SEPARATOR)
                .append(A_TAG)
                .append(SEGMENT_ARG)
                .append(LINE_SEPARATOR)
                .append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" ARG = *(FRAME-3)")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append("frame").append(LINE_SEPARATOR)
                .append("D=M").append(LINE_SEPARATOR).append(A_TAG).append(4)
                .append(LINE_SEPARATOR).append("D=D-A").append(LINE_SEPARATOR)
                .append("A=D").append(LINE_SEPARATOR).append("D=M")
                .append(LINE_SEPARATOR).append(A_TAG).append(SEGMENT_LCL)
                .append(LINE_SEPARATOR).append("M=D")
                .append('\t')
                .append(COMMENT_TAG)
                .append(" LCL = *(FRAME-4)")
                .append(LINE_SEPARATOR)
                .append(A_TAG).append("ret").append(LINE_SEPARATOR)
                .append("A=M").append(LINE_SEPARATOR).append("0;JMP")
                .append('\t').append(COMMENT_TAG).append(" JUMP TO Caller")
                .append(LINE_SEPARATOR);

        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeFunction(final String functionName, final int numLocals) {
        writeLabel(functionName);
        StringBuffer strB = new StringBuffer();
        for (int i = 0; i < numLocals; i++) {
            strB.append(A_TAG).append(SP).append(LINE_SEPARATOR).append("A=M")
                    .append(LINE_SEPARATOR).append("M=0")
                    .append(LINE_SEPARATOR).append(A_TAG).append(SP)
                    .append(LINE_SEPARATOR).append("M=M+1")
                    .append(LINE_SEPARATOR);
        }
        try {
            fWriter.write(strB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        if (fWriter != null) {
            try {
                fWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
