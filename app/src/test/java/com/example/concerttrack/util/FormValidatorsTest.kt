package com.example.concerttrack.util

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class FormValidatorsTest {

    @Test
    fun `validate dateTime return true`() {
        val date = "12-01-2050"
        val time = "20:00"

        val actual = FormValidators.isDateTimeOK(date,time)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate dateTime return false`() {
        val date = "12-01-2018"
        val time = "20:00"

        val actual = FormValidators.isDateTimeOK(date,time)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate header length return false`() {
        val header = "Wydarzenie na stadionie"

        val actual = FormValidators.isHeaderTooLong(header)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate header length return true`() {
        val header = "To jest zdecydowanie za długa nazwa wydarzenia"

        val actual = FormValidators.isHeaderTooLong(header)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate placeName length return false`() {
        val placeName = "Stary Klasztor"

        val actual = FormValidators.isPlaceNameTooLong(placeName)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate placeName length return true`() {
        val placeName = "Stadion Narodowy w Warszawie, ulica Księcia Józefa Poniatowskiego"

        val actual = FormValidators.isHeaderTooLong(placeName)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate header empty return true`() {
        val header = ""

        val actual = FormValidators.isInputEmpty(header)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate header empty return false`() {
        val header = "Wydarzenie na stadionie"

        val actual = FormValidators.isInputEmpty(header)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate fanName length return true`() {
        val fanName = "Katarzyna Elżbieta Kowalska"

        val actual = FormValidators.isFanNameTooLong(fanName)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate fanName length return false`() {
        val fanName = "Ania"

        val actual = FormValidators.isFanNameTooLong(fanName)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate passwords equality return true`() {
        val firstPassword = "123456"
        val secondPassword = "123456"

        val actual = FormValidators.arePasswordsEquals(firstPassword,secondPassword)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate passwords equality return false`() {
        val firstPassword = "123456"
        val secondPassword = "123444"

        val actual = FormValidators.arePasswordsEquals(firstPassword,secondPassword)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate password length return true`() {
        val password = "123"

        val actual = FormValidators.isPasswordTooShort(password)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate password length return false`() {
        val password = "123456"

        val actual = FormValidators.isPasswordTooShort(password)
        Assert.assertEquals(actual,false)
    }

    @Test
    fun `validate name changed return true`() {
        val newName = "Ania"
        val previousName = "Joanna"

        val actual = FormValidators.isNameChanged(newName,previousName)
        Assert.assertEquals(actual,true)
    }

    @Test
    fun `validate name changed return false`() {
        val newName = "Ania"
        val previousName = "Ania"

        val actual = FormValidators.isNameChanged(newName,previousName)
        Assert.assertEquals(actual,false)
    }

}