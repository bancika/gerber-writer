package com.bancika.gerberwriter;

public final class GerberMacros {

  public static final String MACRO_RECTANGLE = 
          "%AMRectangle*\n" +
          "0 Rectangle with straight corners*\n" +
          "0 Uses primitive 4 only, without calculations*\n" +
          "0 $1 xsize/2*\n" +
          "0 $2 ysize/2*\n" +
          "0 $3 rotation angle*\n" +
          "4,1,4,\n" +
          "$1,$2,\n" +
          "$1,-$2,\n" +
          "-$1,-$2,\n" +
          "-$1,$2,\n" +
          "$1,$2,\n" +
          "$3*\n" +
          "%";

  public static final String MACRO_CHAMFERED_RECTANGLE = 
          "%AMChamferedRectangle*\n" +
          "0 Rectangle with chamfered corners*\n" +
          "0 Uses primitive 4 only, without calculations*\n" +
          "0 $1 xsize/2*\n" +
          "0 $2 ysize/2*\n" +
          "0 $3 xsize/2-cutoff*\n" +
          "0 $4 ysize/2-cutoff*\n" +
          "0 $5 rotation angle*\n" +
          "4,1,8,\n" +
          "$3,$2,\n" +
          "$1,$4,\n" +
          "$1,-$4,\n" +
          "$3,-$2,\n" +
          "-$3,-$2,\n" +
          "-$1,-$4,\n" +
          "-$1,$4,\n" +
          "-$3,$2,\n" +
          "$3,$2,\n" +
          "$5*\n" +
          "%";

  public static final String MACRO_ROUNDED_RECTANGLE = 
          "%AMRoundedRectangle*\n" +
          "0 Rectangle with rounded corners*\n" +
          "0 Uses primitive 1 and 4 only, without calculations*\n" +
          "0 $1 xsize/2*\n" +
          "0 $2 ysize/2*\n" +
          "0 $3 xsize/2-radius*\n" +
          "0 $4 ysize/2-radius*\n" +
          "0 $5 rotation angle*\n" +
          "0 $6 diameter*\n" +
          "0 $7 x of center of first quadrant circle*\n" +
          "0 $8 y of center of first quadrant circle*\n" +
          "0 $9 x of center of 2nd quadrant circle*\n" +
          "0 $10 y of center of 2nd quadrant circle*\n" +
          "4,1,8,\n" +
          "$3,$2,\n" +
          "$1,$4,\n" +
          "$1,-$4,\n" +
          "$3,-$2,\n" +
          "-$3,-$2,\n" +
          "-$1,-$4,\n" +
          "-$1,$4,\n" +
          "-$3,$2,\n" +
          "$3,$2,\n" +
          "$5*\n" +
          "1,1,$6,$7,$8*\n" +
          "1,1,$6,-$7,-$8*\n" +
          "1,1,$6,$9,$10*\n" +
          "1,1,$6,-$9,-$10*\n" +
          "%";

  public static final String MACRO_THERMAL = 
          "%AMThermal*\n" +
          "0 Circular thermal with straight corners*\n" +
          "0 $1 outer diameter*\n" +
          "0 $2 inner diameter*\n" +
          "0 $3 gap*\n" +
          "0 $4 rotation angle*\n" +
          "7,0,0,$1,$2,$3,$4*\n" +
          "%";

  public static final String MACRO_ROUNDED_THERMAL = 
          "%AMRoundedThermal*\n" +
          "0 Circular thermal with rounded corners*\n" +
          "0 $1 outer diameter*\n" +
          "0 $2 inner diameter*\n" +
          "0 $3 gap of straight thermal primitive*\n" +
          "0 $4 rotation angle*\n" +
          "0 $5 diameter rounding circles*\n" +
          "0 $6 x coordinate of q1 along h axis circle, rotated*\n" +
          "0 $7 y coordinate of q1 along h axis circle, rotated*\n" +
          "0 $8 x coordinate of q1 along v axis circle, rotated*\n" +
          "0 $9 y coordinate of q1 along v axis circle, rotated*\n" +
          "7,0,0,$1,$2,$3,$4*\n" +
          "1,1,$5,$6,$7*\n" +
          "1,1,$5,-$7,$6*\n" +
          "1,1,$5,-$6,-$7*\n" +
          "1,1,$5,$7,-$6*\n" +
          "1,1,$5,$8,$9*\n" +
          "1,1,$5,-$9,$8*\n" +
          "1,1,$5,-$8,-$9*\n" +
          "1,1,$5,$9,-$8*\n" +
          "1,0,$2,0,0*\n" +
          "%";

  // Private constructor to prevent instantiation
  private GerberMacros() {
      throw new AssertionError("GerberMacros is a utility class and should not be instantiated");
  }
}

//
//    public static final String MACRO_RECTANGLE = """
//            %AMRectangle*
//            0 Rectangle with straight corners*
//            0 Uses primitive 4 only, without calculations*
//            0 $1 xsize/2*
//            0 $2 ysize/2*
//            0 $3 rotation angle*
//            4,1,4,
//            $1,$2,
//            $1,-$2,
//            -$1,-$2,
//            -$1,$2,
//            $1,$2,
//            $3*
//            %""";
//
//    public static final String MACRO_CHAMFERED_RECTANGLE = """
//            %AMChamferedRectangle*
//            0 Rectangle with chamfered corners*
//            0 Uses primitive 4 only, without calculations*
//            0 $1 xsize/2*
//            0 $2 ysize/2*
//            0 $3 xsize/2-cutoff*
//            0 $4 ysize/2-cutoff*
//            0 $5 rotation angle*
//            4,1,8,
//            $3,$2,
//            $1,$4,
//            $1,-$4,
//            $3,-$2,
//            -$3,-$2,
//            -$1,-$4,
//            -$1,$4,
//            -$3,$2,
//            $3,$2,
//            $5*
//            %""";
//
//    public static final String MACRO_ROUNDED_RECTANGLE = """
//            %AMRoundedRectangle*
//            0 Rectangle with rounded corners*
//            0 Uses primitive 1 and 4 only, without calculations*
//            0 $1 xsize/2*
//            0 $2 ysize/2*
//            0 $3 xsize/2-radius*
//            0 $4 ysize/2-radius*
//            0 $5 rotation angle*
//            0 $6 diameter*
//            0 $7 x of center of first quadrant circle*
//            0 $8 y of center of first quadrant circle*
//            0 $9 x of center of 2nd quadrant circle*
//            0 $10 y of center of 2nd quadrant circle*
//            4,1,8,
//            $3,$2,
//            $1,$4,
//            $1,-$4,
//            $3,-$2,
//            -$3,-$2,
//            -$1,-$4,
//            -$1,$4,
//            -$3,$2,
//            $3,$2,
//            $5*
//            1,1,$6,$7,$8*
//            1,1,$6,-$7,-$8*
//            1,1,$6,$9,$10*
//            1,1,$6,-$9,-$10*
//            %""";
//
//    public static final String MACRO_THERMAL = """
//            %AMThermal*
//            0 Circular thermal with straight corners*
//            0 $1 outer diameter*
//            0 $2 inner diameter*
//            0 $3 gap*
//            0 $4 rotation angle*
//            7,0,0,$1,$2,$3,$4*
//            %""";
//
//    public static final String MACRO_ROUNDED_THERMAL = """
//            %AMRoundedThermal*
//            0 Circular thermal with rounded corners*
//            0 $1 outer diameter*
//            0 $2 inner diameter*
//            0 $3 gap of straight thermal primitive*
//            0 $4 rotation angle*
//            0 $5 diameter rounding circles*
//            0 $6 x coordinate of q1 along h axis circle, rotated*
//            0 $7 y coordinate of q1 along h axis circle, rotated*
//            0 $8 x coordinate of q1 along v axis circle, rotated*
//            0 $9 y coordinate of q1 along v axis circle, rotated*
//            7,0,0,$1,$2,$3,$4*
//            1,1,$5,$6,$7*
//            1,1,$5,-$7,$6*
//            1,1,$5,-$6,-$7*
//            1,1,$5,$7,-$6*
//            1,1,$5,$8,$9*
//            1,1,$5,-$9,$8*
//            1,1,$5,-$8,-$9*
//            1,1,$5,$9,-$8*
//            1,0,$2,0,0*
//            %""";
