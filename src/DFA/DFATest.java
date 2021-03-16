package DFA;

public class DFATest {
    public static void main(String[] args) {

        DFAString dfaString = new DFAString("DFAString");
        String dfaStringInput = "\"39840003e_100  _9wea\"";
        boolean dfaStringVerdict = dfaString.evaluateInput(dfaString, dfaStringInput);
        System.out.println("dfaString verdict for " + dfaStringInput + ": " + dfaStringVerdict);

        DFAFloat dfaFloat = new DFAFloat("DFAFloat");
        String dfaFloatInput = "1.2e-234.1";
        boolean dfaFloatVerdict = dfaFloat.evaluateInput(dfaFloatInput);
        System.out.println("dfaFloat verdict for " + dfaFloatInput + ": " + dfaFloatVerdict);

        DFAFraction dfaFraction = new DFAFraction("DFAFraction");
        String dfaFractionInput = ".12345";
        boolean dfaFractionVerdict = dfaFraction.evaluateInput(dfaFraction, dfaFractionInput);
        System.out.println("dfaFraction verdict for " + dfaFractionInput + ": " + dfaFractionVerdict);

        DFAInteger dfaInteger = new DFAInteger("DFAInteger");
        String dfaIntegerInput = "awe_23fa_";
        boolean dfaIntegerVerdict = dfaInteger.evaluateInput(dfaInteger, dfaIntegerInput);
        System.out.println("dfaInteger verdict for " + dfaIntegerInput + ": " + dfaIntegerVerdict);

        DFAId dfaId = new DFAId("DFAId");
        String dfaIdInput = "1.23";
        boolean dfaIdVerdict = dfaId.evaluateInput(dfaId, dfaIdInput);
        System.out.println("dfaId verdict for " + dfaIdInput + ": " + dfaIdVerdict);

        DFA dfaInteger_Id = dfaId.getUnionOfTwoDFA(dfaId, dfaInteger, "dfaInteger_Id");
        String dfaInteger_IdInput = "awe_23fa_";
        boolean dfaInteger_IdVerdict = dfaInteger_Id.evaluateInput(dfaInteger_Id, dfaInteger_IdInput);
        System.out.println("dfaInteger_Id verdict for " + dfaInteger_IdInput + ": " + dfaInteger_IdVerdict);

//        DFA doubleUnion = dfaInteger_Id.getUnionOfTwoDFA(dfaFraction, dfaInteger_Id, "doubleUnion");
//        String doubleUnionInput = ".13203";
//        boolean doubleUnionVerdict = doubleUnion.evaluateInput(doubleUnion, doubleUnionInput);
//        System.out.println("doubleUnion verdict for " + doubleUnionInput + ": " + doubleUnionVerdict);

        //System.out.println(doubleUnion);

    }
}
