/*
 * Copyright 2010-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package codegen.escapeAnalysis.test8

class F(val s: String) {
    var g = F("")
}

class A {
    var f = F("qzz")
}

// PointsTo:
//     P0.f -> D0
//     RET.v@lue -> P0.f
//     RET.v@lue -> D0
//     RET.v@lue -> D0.g
//     D0.g -> P0.f
//     D0.g -> D0
// Escapes:
fun foo(a: A): F {
    a.f = F("zzz")
    a.f.g = a.f
    return a.f.g.g
}

fun main() = println(foo(A()).s)