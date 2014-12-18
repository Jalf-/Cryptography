package com.github.Jalfdash;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLDocumentController implements Initializable
{
	@FXML
	private void pGenerateAction(ActionEvent event)
	{
		generatePrime("p", pInput);
	}
	
	private void generatePrime(String primeID, TextField tf)
	{
		int bits;
		if (bitsInput.getText().equalsIgnoreCase("0") || bitsInput.getText().equalsIgnoreCase("1") ||
				bitsInput.getText().equalsIgnoreCase("2") || bitsInput.getText().equalsIgnoreCase("3"))
		{
			writeInConsole("ERROR: Bits most be more than 3!");
			return;
		}
		
		if (bitsInput.getText().length() == 0)
		{
			bitsInput.setText("1024");
		}
		
		try
		{
			bits = Integer.parseInt(bitsInput.getText());
		}
		catch (Exception e)
		{
			writeInConsole("ERROR: Bits is not a number or is too long!");
			return;
		}
		
		BigInteger prime = BigInteger.probablePrime(bits, new Random());
		
		if (isPAndQEqual(prime, primeID))
		{
			generatePrime(primeID, tf);
			return;
		}
		
		String pStr = prime.toString();
		
		tf.setText(pStr);
		
		writeInConsole(primeID + ": " + pStr);
	}
	
	private boolean isPAndQEqual(BigInteger prime, String primeID)
	{
		if (primeID.equalsIgnoreCase("p"))
		{
			try {
				if (prime.compareTo(new BigInteger(qInput.getText())) == 0) return true;
			} catch (Exception e) {
				return false;
			}
		}
		else if (primeID.equalsIgnoreCase("q"))
		{
			try {
				if (prime.compareTo(new BigInteger(pInput.getText())) == 0) return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	@FXML
	private void qGenerateAction(ActionEvent event)
	{
		generatePrime("q", qInput);
	}
	
	@FXML
	private void nGenerateAction(ActionEvent event)
	{
		primeMultication("n", nInput);
	}
	
	private void primeMultication(String resultID, TextField tf)
	{
		if (pInput.lengthProperty().get() == 0) generatePrime("p", pInput);
		if (qInput.lengthProperty().get() == 0) generatePrime("q", qInput);
		
		BigInteger p;
		BigInteger q;
		
		BigInteger result;
		
		try
		{
			p = new BigInteger(pInput.getText());
			q = new BigInteger(qInput.getText());
			
			if (resultID.equalsIgnoreCase("n")) result = p.multiply(q);
			else result = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
			
		}
		catch (Exception e)
		{
			writeInConsole("ERROR: p and q are not numbers!");
			return;
		}
		
		String resultStr = result.toString();
		
		tf.setText(resultStr);
		
		writeInConsole(resultID + ": " + resultStr);
	}
	
	@FXML
	private void kGenerateAction(ActionEvent event)
	{
		primeMultication("k", kInput);
	}
	
	@FXML
	private void eGenerateAction(ActionEvent event)
	{
		generateE();
	}
	
	private void generateE()
	{
		BigInteger e = new BigInteger("65537");
		
		if (kInput.lengthProperty().get() == 0) primeMultication("k", kInput);
		
		try
		{
			BigInteger k = new BigInteger(kInput.getText());
			
			while(!e.gcd(k).equals(BigInteger.ONE) || e.compareTo(k) == 1)
			{
				if (k.bitLength() < 16)
				{
					e = BigInteger.probablePrime(k.bitLength() - 1, new Random());
				}
				else e = BigInteger.probablePrime(16, new Random());
			}
		}
		catch (Exception e2)
		{
			writeInConsole("ERROR: k (p and q) are not numbers!");
			return;
		}
		
		String eStr = e.toString();
		
		eInput.setText(eStr);
		
		writeInConsole("e: " + eStr);
	}
	
	@FXML
	private void dGenerateAction(ActionEvent event)
	{
		generateD();
	}
	
	private void generateD()
	{
		BigInteger d;
		BigInteger k;
		BigInteger e;
		
		if (kInput.lengthProperty().get() == 0) primeMultication("k", kInput);
		if (eInput.lengthProperty().get() == 0) generateE();
		
		try
		{
			k = new BigInteger(kInput.getText());
		}
		catch (Exception e2)
		{
			writeInConsole("ERROR: k (p and q) are not numbers!");
			return;
		}
		
		try
		{
			e = new BigInteger(eInput.getText());
		}
		catch (Exception e3)
		{
			writeInConsole("ERROR: e is not a number!");
			return;
		}
		
		d = e.modInverse(k);
		
		String dStr = d.toString();
		
		dInput.setText(dStr);
		
		writeInConsole("d: " + dStr);
	}
	
	@FXML
	private void encryptAction(ActionEvent event)
	{
		if (encryptInput.lengthProperty().get() == 0)
		{
			writeInConsole("ERROR: Enter a message to encrypt!");
			return;
		}
		
		BigInteger input = null;
		
		if (nInput.lengthProperty().get() == 0) primeMultication("n", nInput);
		if (eInput.lengthProperty().get() == 0) generateE();
		
		BigInteger n;
		BigInteger e;
		
		try
		{
			n = new BigInteger(nInput.getText());
		}
		catch (Exception e2)
		{
			writeInConsole("ERROR: n (p and q) are not numbers!");
			return;
		}
		
		try
		{
			e = new BigInteger(eInput.getText());
		}
		catch (Exception e3)
		{
			writeInConsole("ERROR: e is not a number!");
			return;
		}
		try
		{
			byte[] bytes = encryptInput.getText().getBytes("US-ASCII");
			
			String inputString = "";
			
			if (encryptInput.lengthProperty().get() > 1)
			{
				inputString += "999";
			}

			for (byte b : bytes)
			{
				inputString += String.format("%03d",  b);;
			}
			
			if (inputString.length() > nInput.getText().length())
			{
				writeInConsole("ERROR: Encryption string is too large or n is too small!");
				return;
			}
			input = new BigInteger(inputString);
		}
		catch (UnsupportedEncodingException e4)
		{
			e4.printStackTrace();
		}
		
		BigInteger cryptText = input.modPow(e, n);
		
		String cryptStr = cryptText.toString();
		
		decryptInput.setText(cryptStr);
		
		writeInConsole("Crypt Text: " + cryptStr);
	}
	
	@FXML
	private void decryptAction(ActionEvent event)
	{
		if (decryptInput.lengthProperty().get() == 0)
		{
			writeInConsole("ERROR: Enter a message to decrypt!");
			return;
		}
		
		BigInteger input = null;
		
		if (nInput.lengthProperty().get() == 0) primeMultication("n", nInput);
		if (dInput.lengthProperty().get() == 0) generateD();
		
		BigInteger n;
		BigInteger d;
		
		try
		{
			n = new BigInteger(nInput.getText());
		}
		catch (Exception e2)
		{
			writeInConsole("ERROR: n (p and q) are not numbers!");
			return;
		}
		
		try
		{
			d = new BigInteger(dInput.getText());
		}
		catch (Exception e3)
		{
			writeInConsole("ERROR: d is not a number!");
			return;
		}
		
		input = new BigInteger(decryptInput.getText());
		
		BigInteger decryptText = input.modPow(d, n);
		
		String decryptString = decryptText.toString();
		
		String decryptFinalStr = "";
		
		if (decryptString.length() < 4) decryptFinalStr = Character.toString((char) Integer.parseInt(decryptString));
		else
		{
			for (int i = 3; i < decryptString.length() - 1; i += 3)
			{
				String sign = String.valueOf(decryptString.charAt(i)) + 
						String.valueOf(decryptString.charAt(i+1)) +
						String.valueOf(decryptString.charAt(i+2));
				
				decryptFinalStr += Character.toString((char) Integer.parseInt(sign));
			}
		}
		writeInConsole("Decrypt Text: " + decryptFinalStr);
	}
	
	private void writeInConsole(String str)
	{
		console.appendText(str + "\n");
		console.setScrollTop(Double.MAX_VALUE);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
	}

    @FXML // fx:id="console"
    private TextArea console; // Value injected by FXMLLoader

    @FXML // fx:id="dGenerate"
    private Button dGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="encryptInput"
    private TextField encryptInput; // Value injected by FXMLLoader

    @FXML // fx:id="nGenerate"
    private Button nGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="decryptInput"
    private TextField decryptInput; // Value injected by FXMLLoader

    @FXML // fx:id="pInput"
    private TextField pInput; // Value injected by FXMLLoader

    @FXML // fx:id="qGenerate"
    private Button qGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="nInput"
    private TextField nInput; // Value injected by FXMLLoader

    @FXML // fx:id="kInput"
    private TextField kInput; // Value injected by FXMLLoader
    
    @FXML // fx:id="bitsInput"
    private TextField bitsInput; // Value injected by FXMLLoader

    @FXML // fx:id="eInput"
    private TextField eInput; // Value injected by FXMLLoader

    @FXML // fx:id="qInput"
    private TextField qInput; // Value injected by FXMLLoader

    @FXML // fx:id="encryptButton"
    private Button encryptButton; // Value injected by FXMLLoader

    @FXML // fx:id="pGenerate"
    private Button pGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="eGenerate"
    private Button eGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="dInput"
    private TextField dInput; // Value injected by FXMLLoader

    @FXML // fx:id="kGenerate"
    private Button kGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="decryptButton"
    private Button decryptButton; // Value injected by FXMLLoader
}
