package com.ksnk.test

import io.realm.kotlin.types.RealmObject

 class FragmentEntity(var id:Int, var testFragment: TestFragment): RealmObject {
    constructor() : this(0, TestFragment())

     override fun toString(): String {
         return "FragmentEmpty(id=$id, testFragment=$testFragment)"
     }
 }