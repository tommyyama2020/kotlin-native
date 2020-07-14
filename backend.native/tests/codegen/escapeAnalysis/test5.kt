/*
 * Copyright 2010-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package codegen.escapeAnalysis.test5

class A(val s: String)
class B {
    var f: A = A("qzz")
}
class C {
    var g: B = B()
}

// PointsTo:
//     RET.v@lue -> D0
//     D0.f -> P0.g.f
// Escapes:
fun foo(c1: C, c2: C): B {
    val b = B()
    b.f = c1.g.f
    return b
}

fun main() = println(foo(C(), C()))