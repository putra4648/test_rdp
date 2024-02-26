package com.pradana.test_rdp;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestRdpApplicationTests {

	@Test
	public void testDateFromLong() {
		Date date = new Date(Long.valueOf(1708667294461L));

		System.out.println(date.after(new Date()));

	}

}
