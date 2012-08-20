/*
 * Copyright (c) 2010-2012 BlinxBox.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.blinxbox.restinstagram.exception;

/**
 * Indicates that an error occurred while mapping JSON data to a Java object.
 * 
 * @author Efi MK
 */
public class InstagramJsonMappingException extends InstagramException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception with the given message.
   * 
   * @param message
   *          A message describing this exception.
   */
  public InstagramJsonMappingException(String message) {
    super(message);
  }

  /**
   * Creates an exception with the given message and cause.
   * 
   * @param message
   *          A message describing this exception.
   * @param cause
   *          The exception that caused this exception to be thrown.
   */
  public InstagramJsonMappingException(String message, Throwable cause) {
    super(message, cause);
  }
}