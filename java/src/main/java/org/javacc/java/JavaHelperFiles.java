
package org.javacc.java;

import org.javacc.Version;
import org.javacc.jjtree.JJTreeGlobals;
import org.javacc.parser.CodeGeneratorSettings;
import org.javacc.parser.JavaCCErrors;
import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.LexGen;
import org.javacc.parser.MetaParseException;
import org.javacc.parser.Options;
import org.javacc.parser.RStringLiteral;
import org.javacc.parser.RegExprSpec;
import org.javacc.parser.RegularExpression;
import org.javacc.parser.TokenProduction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link JavaHelperFiles} class.
 */
public abstract class JavaHelperFiles {

  /**
   * Constructs an instance of {@link JavaHelperFiles}.
   */
  private JavaHelperFiles() {}


  public static void genMiscFile(String fileName, String templatePath) throws Error {
    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.of(Options.getOptions()))) {
      builder.setFile(new File(Options.getOutputDirectory(), fileName));

      /*
       * cba -- 2013/07/22 -- previously wired to a typo version of this option
       * -- KEEP_LINE_COL
       */
      builder.setVersion(Version.version).addTools(JavaCCGlobals.toolName)
          .addOption(Options.USEROPTION__KEEP_LINE_COLUMN);

      builder.setPackageName(JavaUtil.parsePackage());
      builder.printTemplate(templatePath);
    } catch (IOException e) {
      System.err.println("Failed to create " + fileName + " " + e);
      JavaCCErrors.semantic_error("Could not open file " + fileName + " for writing.");
      throw new Error();
    }
  }

  public static void gen_Token() {
    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.of(Options.getOptions()))) {
      builder.setFile(new File(Options.getOutputDirectory(), "Token.java"));
      builder.setVersion(Version.version).addTools(JavaCCGlobals.toolName);
      builder.setPackageName(JavaUtil.parsePackage());

      /*
       * cba -- 2013/07/22 -- previously wired to a typo version of this option
       * -- KEEP_LINE_COL
       */
      builder.addOption(Options.USEROPTION__TOKEN_EXTENDS, Options.USEROPTION__KEEP_LINE_COLUMN,
          Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);

      builder.printTemplate("/templates/Token.template");
    } catch (IOException e) {
      System.err.println("Failed to create Token " + e);
      JavaCCErrors.semantic_error("Could not open file Token.java for writing.");
      throw new Error();
    }
  }


  public static void gen_TokenManager() {
    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.of(Options.getOptions()))) {
      builder.setFile(new File(Options.getOutputDirectory(), "TokenManager.java"));
      builder.setVersion(Version.version).addTools(JavaCCGlobals.toolName)
          .addOption(Options.USEROPTION__SUPPORT_CLASS_VISIBILITY_PUBLIC);
      builder.setPackageName(JavaUtil.parsePackage());
      builder.printTemplate("/templates/TokenManager.template");
    } catch (IOException e) {
      System.err.println("Failed to create TokenManager " + e);
      JavaCCErrors.semantic_error("Could not open file TokenManager.java for writing.");
      throw new Error();
    }
  }

  public static void gen_Constants() throws MetaParseException {
    if (JavaCCErrors.get_error_count() != 0)
      throw new MetaParseException();

    List<String> toolnames = new ArrayList<String>(JavaCCGlobals.toolNames);
    toolnames.add(JavaCCGlobals.toolName);

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(CodeGeneratorSettings.create())) {
      builder.setFile(new File(Options.getOutputDirectory(), JavaCCGlobals.cu_name + "Constants.java"));
      builder.setPackageName(JavaUtil.parsePackage());
      builder.addTools(toolnames.toArray(new String[toolnames.size()]));

      builder.println();
      builder.println("/**");
      builder.println(" * Token literal values and constants.");
      builder.println(" * Generated by org.javacc.parser.OtherFilesGen#start()");
      builder.println(" */");

      if (Options.getSupportClassVisibilityPublic()) {
        builder.print("public ");
      }
      builder.println("interface " + JavaCCGlobals.cu_name + "Constants {");
      builder.println();

      builder.println("  /** End of File. */");
      builder.println("  int EOF = 0;");
      for (RegularExpression re : JavaCCGlobals.ordered_named_tokens) {
        builder.println("  /** RegularExpression Id. */");
        builder.println("  int " + re.label + " = " + re.ordinal + ";");
      }
      builder.println();
      if (!Options.getUserTokenManager() && Options.getBuildTokenManager()) {
        for (int i = 0; i < LexGen.lexStateName.length; i++) {
          builder.println("  /** Lexical state. */");
          builder.println("  int " + LexGen.lexStateName[i] + " = " + i + ";");
        }
        builder.println();
      }
      builder.println("  /** Literal token values. */");
      builder.println("  String[] tokenImage = {");
      builder.println("    \"<EOF>\",");

      for (TokenProduction tp : JavaCCGlobals.rexprlist) {
        for (RegExprSpec res : tp.respecs) {
          builder.print("    ");
          if (res.rexp instanceof RStringLiteral) {
            builder.println("\"\\\""
                + JavaCCGlobals.add_escapes(JavaCCGlobals.add_escapes(((RStringLiteral) res.rexp).image)) + "\\\"\",");
          } else if (!res.rexp.label.equals("")) {
            builder.println("\"<" + res.rexp.label + ">\",");
          } else {
            if (res.rexp.tpContext.kind == TokenProduction.TOKEN) {
              JavaCCErrors.warning(res.rexp,
                  "Consider giving this non-string token a label for better error reporting.");
            }
            builder.println("\"<token of kind " + res.rexp.ordinal + ">\",");
          }
        }
      }
      builder.println("  };");
      builder.println("}");
    } catch (java.io.IOException e) {
      JavaCCErrors.semantic_error("Could not open file " + JavaCCGlobals.cu_name + "Constants.java for writing.");
      throw new Error();
    }
  }

  public static void generateSimple(String template, String outputFileName, CodeGeneratorSettings settings)
      throws IOException {
    File file = new File((String) settings.get("OUTPUT_DIRECTORY"), outputFileName);

    try (JavaCodeBuilder builder = JavaCodeBuilder.of(settings)) {
      builder.setFile(file);
      builder.setPackageName(JavaUtil.parsePackage());
      builder.printTemplate(template);
    }
  }
}
