package com.tangblack.iloveck101.test;

import static org.junit.Assert.*

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.tangblack.iloveck101.ILoveCk101;

class ILoveCk101Test extends GroovyTestCase
{
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testRunWithAThread()
	{
		ILoveCk101 iLoveCk101 = new ILoveCk101()
		iLoveCk101.run('http://ck101.com/thread-2876990-1-1.html')
	}
	
	@Test
	public void testRunWithAThreadList()
	{
		ILoveCk101 iLoveCk101 = new ILoveCk101()
		iLoveCk101.run('http://ck101.com/beauty/')
	}

}
