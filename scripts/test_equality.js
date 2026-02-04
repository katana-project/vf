import { decompile } from "../dist/vf.js";
import { readFile } from "node:fs/promises";

const name = "sample/inheritance/Diamond";
const expected = `package sample.inheritance;

public class Diamond {
   public static void main(String[] args) {
      case1();
      System.out.println("");
      case2();
      System.out.println("");
      case3();
      System.out.println("");
      case4();
   }

   private static void case1() {
      Diamond.ChildC c = new Diamond.ChildC();
      c.action();
   }

   private static void case2() {
      Diamond.ChildC c = new Diamond.ChildC();
      c.action();
      c.action();
   }

   private static void case3() {
      Diamond.ChildB b = new Diamond.ChildC();
      ((Diamond.ChildC)b).doA();
   }

   private static void case4() {
      try {
         Diamond.ChildB b = new Diamond.ChildB() {};
         ((Diamond.ChildC)b).doA();
      } catch (ClassCastException var1) {
         System.out.println("case4: successfully thrown ClassCastException");
      }
   }

   interface ChildA extends Diamond.Root {
      @Override
      default void action() {
         this.doA();
      }

      default void doA() {
         System.out.println("A");
      }
   }

   interface ChildB extends Diamond.Root {
      @Override
      default void action() {
         this.doB();
      }

      default void doB() {
         System.out.println("B");
      }
   }

   static class ChildC implements Diamond.ChildA, Diamond.ChildB {
      @Override
      public void action() {
         this.actionSubA();
         this.actionSubB();
      }

      private void actionSubA() {
         Diamond.ChildA.super.action();
      }

      private void actionSubB() {
         Diamond.ChildB.super.action();
      }
   }

   interface Root {
      void action();
   }
}
`;

const result = await decompile(name, {
    source: async (name) => {
        try {
            const data = await readFile(`../samples/${name}.class`);
            console.log(`read source for ${name}`);
            return new Uint8Array(data);
        } catch (e) {
            console.error(`failed to read source for ${name}`);
        }

        return null;
    },
    resources: [
        "sample/inheritance/Diamond",
        "sample/inheritance/Diamond$1",
        "sample/inheritance/Diamond$ChildA",
        "sample/inheritance/Diamond$ChildB",
        "sample/inheritance/Diamond$ChildC",
        "sample/inheritance/Diamond$Root",
    ],
});

console.log(result[name]);
if (result[name] !== expected) {
    console.error("decompilation result does not match expected output");
} else {
    console.log("decompilation result matches expected output");
}
