package co.orffosoft.rest.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import co.orffosoft.dto.QrCodeDTO;
import co.orffosoft.dto.QrCodeItemDTO;
import lombok.extern.log4j.Log4j2;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Log4j2
@Service
public class QRCodeUtil {

	public static void main(String[] args) {

		QRCodeUtil qrCodeUtil = new QRCodeUtil();

		qrCodeUtil.testQrCodeGeneration();

		

	}

	//
	// public void createQrCodePdf(ProductQRCodeDetailsDTO productQRCodeDetailsDTO,
	// List<ItemQRCodeDetailsDTO> itemQRCodeDetailsList, Double discountPercent)
	// throws IOException, DocumentException {
	//
	// // ItemQRCodeDetailsDTO itemQRCodeDetailsDTO = itemQRCodeDetailsList.get(0);
	// log.info("<==== Start PDFUtil.createQrCodePdf ====>");
	// log.info("productQRCodeDetailsDTO : " + productQRCodeDetailsDTO);
	// // log.info("itemQRCodeDetailsDTO : "+itemQRCodeDetailsDTO);
	//
	// String qrCodePath =
	// productQRCodeZipService.getAppKeyValue("PRODUCT_QR_CODE_PATH");
	// // qrCodePath = "/home/user1/Documents/orffosoft/QR_CODE/";
	// if (StringUtils.isEmpty(qrCodePath)) {
	// log.info("ItemQRCodeService createQRImage qrCodePath is empty ");
	// throw new
	// RestException(ErrorDescription.PRODUCT_QR_CODE_IMAGE_PATH_NOT_FOUND);
	// }
	//
	// String directoryFolder = qrCodePath +
	// productQRCodeDetailsDTO.getQrCodeDirectoryPath();
	//
	// if (directoryFolder.charAt(directoryFolder.length() - 1) != '/') {
	// directoryFolder = directoryFolder + "/";
	// }
	//
	// File directory = new File(directoryFolder);
	//
	// log.info("directory For QRCode Image : " + directoryFolder);
	//
	// if (!directory.exists()) {
	// directory.mkdirs();
	// }
	//
	// String itemName = productQRCodeDetailsDTO.getProductVarietyCode() + "_"
	// + itemQRCodeDetailsList.get(0).getProductVarietyName();
	//
	// URL englishFontUrl =
	// PDFUtil.class.getClassLoader().getResource("iText/PlayfairDisplay-Regular.ttf");
	// String englishFont = englishFontUrl.getPath();
	//
	// Font generalFont = FontFactory.getFont(englishFont, 40, Font.BOLD);
	//
	// String pdfFilePath = directoryFolder + File.pathSeparator + itemName +
	// "_qrcode.pdf";
	//
	// Document document = new Document();
	// document.setMargins(40, 12, 2, 2);
	// PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	// document.open();
	//
	// if (itemQRCodeDetailsList != null)
	// log.info("product quantity::::::" + itemQRCodeDetailsList.size());
	// else
	// log.info("itemQRCodeDetailsList is null");
	//
	// for (ItemQRCodeDetailsDTO itemQRCodeDetailsDTO : itemQRCodeDetailsList) {
	//
	// RetailQualityCheck retailQualityCheck = retailQualityCheckRepository
	// .findQualityCheckByStockReferenceNumber(productQRCodeDetailsDTO.getInwardNumber());
	//
	// itemQRCodeDetailsDTO.setPurhcasePrice(null);
	//
	// String age = null;
	// if (retailQualityCheck != null) {
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-yy");
	// age =
	// simpleDateFormat.format(retailQualityCheck.getCreatedDate()).replace("-",
	// "");
	// }
	//
	// itemQRCodeDetailsDTO.setProductAge(age);
	//
	// document.newPage();
	// String productName = productQRCodeDetailsDTO
	// .getProductVarietyCode() /* + " / " +
	// itemQRCodeDetailsDTO.getProductVarietyName() */;
	// Gson gson = new Gson();
	// log.info("itemQRCodeDetailsDTO :::::" + itemQRCodeDetailsDTO);
	//
	// String jsonText = gson.toJson(itemQRCodeDetailsDTO);
	//
	// log.info("jsonText :::" + jsonText);
	//
	// ByteArrayOutputStream outputStream =
	// QRCode.from(jsonText).to(ImageType.PNG).withSize(200, 200).stream();
	//
	// Image img = Image.getInstance(outputStream.toByteArray());
	//
	// float[] columnWidths = { 7, 8 };
	//
	// PdfPTable table = new PdfPTable(1);
	// table.setWidthPercentage(75);
	//
	// PdfPCell cell = new PdfPCell(img, true);
	// cell.setBorder(Rectangle.NO_BORDER);
	// table.addCell(cell);
	//
	// document.add(table);
	//
	// Font qrCodeFont = FontFactory.getFont(englishFont, 35, Font.BOLD);
	//
	// // Image icon =
	// //
	// Image.getInstance(PDFUtil.class.getClassLoader().getResource("images/co-optex-thumb-logo-blk-lg.png"));
	// // icon.setAbsolutePosition(1f, 1f);
	//
	// // document.add(icon);
	//
	// Paragraph p = new Paragraph(itemQRCodeDetailsDTO.getQRCode().trim(),
	// qrCodeFont);
	// p.setAlignment(Element.ALIGN_CENTER);
	// document.add(p);
	//
	// document.add(new Phrase("\n"));
	//
	// PdfPTable table2 = new PdfPTable(columnWidths);
	// table2.setWidthPercentage(100);
	//
	// PdfPCell cell2 = null;
	//
	// cell2 = new PdfPCell(new Phrase("AT Number", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase(": " +
	// itemQRCodeDetailsDTO.getATNumber().trim(), generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase("MRP", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// NumberFormat numberFormat = new DecimalFormat("#0.00");
	//
	// String retailPriceStr = "0";
	//
	// Double retailPrice = new Double(itemQRCodeDetailsDTO.getRetailPrice());
	//
	// if (itemQRCodeDetailsDTO.getRetailPrice() != null)
	// retailPriceStr = numberFormat.format(retailPrice);
	//
	// cell2 = new PdfPCell(new Phrase(": Rs." + retailPriceStr, generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase("Net Price", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// Double netPrice = new Double(itemQRCodeDetailsDTO.getRetailPrice());
	//
	// if (discountPercent != null && discountPercent > 0) {
	// netPrice = netPrice - (netPrice * (discountPercent / 100));
	// }
	//
	// String netPriceStr = "0";
	//
	// if (netPrice != null)
	// netPriceStr = numberFormat.format(netPrice);
	//
	// cell2 = new PdfPCell(new Phrase(": Rs." + netPriceStr, generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase("Age", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase(": " + age, generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// document.add(table2);
	//
	// document.add(new Phrase("\n"));
	//
	// Font gstFont = FontFactory.getFont(englishFont, 25, Font.BOLD);
	// Paragraph p2 = new Paragraph("(GST Applicable)", gstFont);
	// p2.setAlignment(Element.ALIGN_CENTER);
	// document.add(p2);
	//
	// document.add(new Phrase("\n"));
	//
	// Font productNameFont = FontFactory.getFont(englishFont, 30, Font.BOLD);
	// Paragraph p3 = new Paragraph(productName.trim(), productNameFont);
	// p3.setAlignment(Element.ALIGN_CENTER);
	//
	// document.add(p3);
	//
	// }
	//
	// document.close();
	//
	// // convertPdfToImage(directoryFolder, pdfFilePath);
	//
	// log.info("<==== End PDFUtil.createQrCodePdf ====>");
	//
	// }
	//
	public void createBarCodePng(String atNumber) throws IOException, DocumentException {

		log.info("<==== Start PDFUtil.createBarCodePng ====>");
		log.info("atNumber : " + atNumber);

		String barcodeString = atNumber;
		Code128Bean barcode128Bean = new Code128Bean();

		barcode128Bean.doQuietZone(false);

		final int dpi = 100;
		String pathimg = atNumber + "_barcode.png";

		// Open output file
		File outputFile = new File(pathimg);
		OutputStream out = new FileOutputStream(outputFile);
		try {
			/*
			 * BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider( out,
			 * "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			 */

			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(out, "image/x-png", dpi,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);

			barcode128Bean.generateBarcode(canvasProvider, barcodeString);

			canvasProvider.finish();
		} finally {
			out.close();
		}

	}

	//
	// public String createQRCodePdf(List<InventoryItems> inventoryItems) throws
	// IOException, DocumentException {
	// Double discountPercent = 20.0;
	// final String UOM_CODE = "METR";
	//
	// log.info(" PDFUtil ::: createQRCodePdf Started..");
	// log.info("APP KEY ::: " + "GENERATE_OPENING_STOCK_QR_CODE_PATH");
	// AppConfig appConfig =
	// appConfigRepository.findByAppKey("GENERATE_OPENING_STOCK_QR_CODE_PATH");
	// String qrCodePath = appConfig.getAppValue();
	// log.info(" QRCodePath ===>>> " + qrCodePath);
	// if (StringUtils.isEmpty(qrCodePath)) {
	// log.info(" PDFUtil :: createQRCodePdf QRCodePath is empty");
	// throw new RestException("QRCode image path is not available");
	// }
	//
	// File directory = new File(qrCodePath);
	//
	// if (!directory.exists()) {
	// log.info(qrCodePath + "this path not available in server");
	// throw new RestException(qrCodePath + " not available in server.");
	// // directory.mkdirs();
	// }
	// Date date = new Date();
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	// String currentDate = dateFormat.format(date);
	// URL englishFontUrl =
	// PDFUtil.class.getClassLoader().getResource("iText/PlayfairDisplay-Regular.ttf");
	//
	// String englishFont = englishFontUrl.getPath();
	//
	// Font generalFont = FontFactory.getFont(englishFont, 40, Font.BOLD);
	//
	// String pdfFilePath = qrCodePath + currentDate + "_qrcode.pdf";
	//
	// log.info("QR_PDF_FilePath ====>>> " + pdfFilePath);
	//
	// Document document = new Document();
	// document.setMargins(40, 12, 2, 2);
	// PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	// document.open();
	//
	// for (InventoryItems inventoryItemsobj : inventoryItems) {
	// QRCodePdfGenerationDTO qrCodeGenerationDTO = new QRCodePdfGenerationDTO();
	// String productName = "";
	// String productCode = null;
	// Long productVarietyId = null;
	// Double unitRate = null;
	// String uomCode = null;
	// String productAge = "";
	// String productAgeName = "";
	// Double openingBalance = inventoryItemsobj.getOpeningBalance();
	// qrCodeGenerationDTO.setATNumber(inventoryItemsobj.getAtNumber());
	// qrCodeGenerationDTO.setQRCode(inventoryItemsobj.getQrCode());
	// if (inventoryItemsobj.getProductVarietyMaster() != null) {
	// productName = inventoryItemsobj.getProductVarietyMaster().getCode();
	// productCode = inventoryItemsobj.getProductVarietyMaster().getName();
	// productVarietyId = inventoryItemsobj.getProductVarietyMaster().getId();
	// if (inventoryItemsobj.getProductVarietyMaster().getUomMaster() != null) {
	// UomMaster uomMaster =
	// inventoryItemsobj.getProductVarietyMaster().getUomMaster();
	// uomCode = uomMaster.getCode();
	// }
	// qrCodeGenerationDTO.setProductVarietyCode(productName);
	// qrCodeGenerationDTO.setProductVarietyName(productCode);
	// }
	// /*** Monthly List in alphabets format ***/
	// Map<String, String> monthAgeList = AppUtil.getAllMonthWithAlphabet();
	// /*** Find UnitRate By StockTransfer And ProductVarietyMaster ***/
	// if (inventoryItemsobj.getStockTransferInward() != null && productVarietyId !=
	// null) {
	// StockTransferItems stockTransferItems = stockTransferItemsRepository
	// .findStockTransferItemByStockTransferAndProduct(
	// inventoryItemsobj.getStockTransferInward().getId(), productVarietyId);
	// if (stockTransferItems != null) {
	// unitRate = stockTransferItems.getUnitRate();
	//
	// if (stockTransferItems.getMonthAge() != null &&
	// stockTransferItems.getYearAge() != null) {
	// String month = monthAgeList.get(stockTransferItems.getMonthAge());
	// month = month == null ? "" : month.trim();
	// String year = stockTransferItems.getYearAge() == null ? "" :
	// stockTransferItems.getYearAge();
	// productAge = month.concat(year);
	// if (!productAge.isEmpty() && !productName.isEmpty()) {
	// productAgeName = productAge + "-" + productName;
	// }
	// }
	// }
	// }
	//
	// log.info(" ProductName ::: " + productName + " Opening Balance ::: " +
	// openingBalance);
	// log.info(" UOM_CODE ===>>> " + uomCode);
	// log.info(" ProductAge and Name ::: " + productAgeName);
	// if (openingBalance != null) {
	// int openingStock = openingBalance.intValue();
	// int opening = 0;
	// while (opening < openingStock) {
	// opening++;
	//
	// document.newPage();
	//
	// Gson gson = new Gson();
	// log.info("itemQRCodeDetailsDTO :::::" + qrCodeGenerationDTO);
	//
	// String jsonText = gson.toJson(qrCodeGenerationDTO);
	// log.info("JsonText ::: " + jsonText);
	//
	// ByteArrayOutputStream outputStream =
	// QRCode.from(jsonText).to(ImageType.PNG).withSize(200, 200)
	// .stream();
	//
	// Image img = Image.getInstance(outputStream.toByteArray());
	//
	// float[] columnWidths = { 7, 8 };
	//
	// PdfPTable table = new PdfPTable(1);
	// table.setWidthPercentage(75);
	//
	// PdfPCell cell = new PdfPCell(img, true);
	// cell.setBorder(Rectangle.NO_BORDER);
	// table.addCell(cell);
	//
	// document.add(table);
	//
	// Font qrCodeFont = FontFactory.getFont(englishFont, 35, Font.BOLD);
	//
	// Paragraph p = new Paragraph(inventoryItemsobj.getQrCode().trim(),
	// qrCodeFont);
	// p.setAlignment(Element.ALIGN_CENTER);
	// document.add(p);
	//
	// document.add(new Phrase("\n"));
	//
	// PdfPTable table2 = new PdfPTable(columnWidths);
	// table2.setWidthPercentage(100);
	//
	// PdfPCell cell2 = null;
	//
	// cell2 = new PdfPCell(new Phrase("AT Number", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase(": " +
	// inventoryItemsobj.getAtNumber().trim(), generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase("MRP", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// NumberFormat numberFormat = new DecimalFormat("#0");
	//
	// /*** Retail Price Calculation ***/
	// String retailPriceStr = "";
	//
	// if (unitRate != null)
	// retailPriceStr = numberFormat.format(unitRate);
	//
	// // log.info("UNIT RATE ::: " + unitRate +" Retail Price ::: " +
	// retailPriceStr);
	//
	// cell2 = new PdfPCell(new Phrase(": Rs." + retailPriceStr, generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// cell2 = new PdfPCell(new Phrase("Net Price", generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// /*** Net Price Calculation ***/
	//
	// Double netPrice = unitRate == null ? 0.0 : unitRate;
	// if (discountPercent != null && discountPercent > 0) {
	// netPrice = netPrice - (netPrice * (discountPercent / 100));
	// }
	//
	// String netPriceStr = "";
	// if (netPrice != null) {
	// netPriceStr = numberFormat.format(netPrice);
	// }
	//
	// // log.info(" Net Price ::: " + netPriceStr);
	// cell2 = new PdfPCell(new Phrase(": Rs." + netPriceStr, generalFont));
	// cell2.setBorder(Rectangle.NO_BORDER);
	// table2.addCell(cell2);
	//
	// /*
	// * cell2 = new PdfPCell(new Phrase("Age", generalFont));
	// * cell2.setBorder(Rectangle.NO_BORDER); table2.addCell(cell2);
	// */
	//
	// // String age = "0";
	//
	// /*
	// * cell2 = new PdfPCell(new Phrase(": " + age, generalFont));
	// * cell2.setBorder(Rectangle.NO_BORDER); table2.addCell(cell2);
	// */
	//
	// document.add(table2);
	//
	// document.add(new Phrase("\n"));
	//
	// Font gstFont = FontFactory.getFont(englishFont, 25, Font.BOLD);
	// Paragraph p2 = new Paragraph("(GST Applicable)", gstFont);
	// p2.setAlignment(Element.ALIGN_CENTER);
	// document.add(p2);
	//
	// document.add(new Phrase("\n"));
	//
	// Font productNameFont = FontFactory.getFont(englishFont, 30, Font.BOLD);
	// Paragraph p3 = new Paragraph(productAgeName, productNameFont);
	// p3.setAlignment(Element.ALIGN_CENTER);
	//
	// document.add(p3);
	//
	// /*** Check if Meters or other values ***/
	// if (UOM_CODE.equalsIgnoreCase(uomCode)) {
	// break;
	// }
	//
	// }
	// }
	// }
	// document.close();
	// log.info(" PdfUtil ::: createQRCodePdf Ended..");
	// return pdfFilePath;
	// }

	private void testQrCodeGeneration() {
		QrCodeDTO qrCodeDataDTO = new QrCodeDTO();

		String qrCodeJsonTxt = "{\"qRCode\":\"16501808SCHB7501\",\"productVarietyCode\":\"SCHB\",\"productVarietyName\":\"SAREES COIMBATORE\"}";

		try {
			List<QrCodeItemDTO> qrCodeItemDTOList = new ArrayList<>();

			QrCodeItemDTO qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("E18-SCHB");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808SCHB7501");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.1590");
			qrCodeItemDTO.setValueThree("Rs.1272");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808TSWX7473\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"TSWX\",\"productVarietyName\":\"TOWELS SALEM_TSWX\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("G08-TSWX");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("OB16501808TSWX7473");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.103");
			qrCodeItemDTO.setValueThree("Rs.82");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808HRN510787\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"HRN5\",\"productVarietyName\":\"42\" HALF SLEEVE SHIRT\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C17-HRN5");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808HRN510787");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.1110");
			qrCodeItemDTO.setValueThree("Rs.888");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808TLK410788\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"TLK4\",\"productVarietyName\":\"TIRUNELVELI KASI TOWEL 27X54\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("E18-TLK4");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808TLK410788");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.87");
			qrCodeItemDTO.setValueThree("Rs.70");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			//
			qrCodeJsonTxt = "{\"qRCode\":\"16501808TLK510789\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"TLK5\",\"productVarietyName\":\"TIRUNELVELI KASI TOWEL 30X60\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("E18-TLK5");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808TLK510789");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.101");
			qrCodeItemDTO.setValueThree("Rs.81");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808ESL110792\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"ESL1\",\"productVarietyName\":\"COTTON PRINTED STOLE 60X180CM\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("E18-ODE1");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808ESL110792");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.420");
			qrCodeItemDTO.setValueThree("Rs.336");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808YSPE10794\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"YSPE\",\"productVarietyName\":\"POWERLOOM POLYCOT SAREES\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C18-YSPE");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808YSPE10794");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.1150");
			qrCodeItemDTO.setValueThree("Rs.920");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808ZSKB10795\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"ZSKB\",\"productVarietyName\":\"KALAMKARI PRINTED COTTON SAREE\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("G18-ZSKB");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808ZSKB10795");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.790");
			qrCodeItemDTO.setValueThree("Rs.632");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			//
			qrCodeJsonTxt = "{\"qRCode\":\"16501808YMDS10796\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"YMDS\",\"productVarietyName\":\"MENS COMBO PACK 2.00MTS DHOTHY\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("A18-YMDS");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808YMDS10796");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.570");
			qrCodeItemDTO.setValueThree("Rs.456");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808EBS68755\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"EBS6\",\"productVarietyName\":\"EXPORT GOODS BEDSNEET\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C18-EBS6");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808EBS68755");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.1240");
			qrCodeItemDTO.setValueThree("Rs.992");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808HRB28618\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"HRB2\",\"productVarietyName\":\"HL READYMADE WHITE SHIRT 38 FS\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C17-HRB2");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808HRB28618");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.880");
			qrCodeItemDTO.setValueThree("Rs.704");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808RMS110791\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"RMS1\",\"productVarietyName\":\"WOVEN DRESS MADURAI MATERIAL\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("F18-RMS1");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808RMS110791");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.800");
			qrCodeItemDTO.setValueThree("Rs.640");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			//
			qrCodeJsonTxt = "{\"qRCode\":\"16501808DMAX7424\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"DMAX\",\"productVarietyName\":\"DHOTHIES MADURAI AVERAGE_Discount\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C17-DMAX");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808DMAX7424");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.265");
			qrCodeItemDTO.setValueThree("Rs.212");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808SCOX7379\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"SCOX\",\"productVarietyName\":\"COTTON SILK KORA BLOUSE\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("K17-SCOX");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808SCOX7379");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.3240");
			qrCodeItemDTO.setValueThree("Rs.2592");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808SDEX7398\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"SDEX\",\"productVarietyName\":\"SAREES_SDEX\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C15-SDEX");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808SDEX7398");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.2660");
			qrCodeItemDTO.setValueThree("Rs.2128");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeJsonTxt = "{\"qRCode\":\"16501808SGNX7399\",\"aTNumber\":\"000000\",\"productVarietyCode\":\"SGNX\",\"productVarietyName\":\"SAREES NEGAM 6 YARDS\"}";
			qrCodeItemDTO = new QrCodeItemDTO();
			qrCodeItemDTO.setQrCodeJsonText(qrCodeJsonTxt);
			qrCodeItemDTO.setLabelFive("C15-SGNX");
			qrCodeItemDTO.setLabelFour("(GST Applicable)");
			qrCodeItemDTO.setLabelOne("AT Number");
			qrCodeItemDTO.setLabelThree("Net Price");
			qrCodeItemDTO.setLabelTwo("MRP");
			qrCodeItemDTO.setQrCode("16501808SGNX7399");
			qrCodeItemDTO.setValueOne("000000");
			qrCodeItemDTO.setValueTwo("Rs.3430");
			qrCodeItemDTO.setValueThree("Rs.2744");
			qrCodeItemDTOList.add(qrCodeItemDTO);

			qrCodeDataDTO.setPdfFilePath("E:\\qrCode.pdf");
			qrCodeDataDTO.setQrCodeItemDTOList(qrCodeItemDTOList);
			//
			// generateQrCodePdfFile(qrCodeDataDTO);

			generateQrCodePdfFileWithFooterImage(qrCodeDataDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param qrCodeDataDTO
	 * @throws Exception
	 */
	private void generateQrCodePdfFileWithFooterImage(QrCodeDTO qrCodeDataDTO) throws Exception {

		ByteArrayOutputStream outputStream = null;

		final int fontSizeOne = 8;

		final int fontSizeTwo = 12;

		// final Phrase returnPhrase = new Phrase("\r");

		final Phrase returnPhrase = null;

		List<QrCodeItemDTO> qrCodeItemDTOList = qrCodeDataDTO.getQrCodeItemDTOList();

		if (qrCodeItemDTOList == null || qrCodeItemDTOList.isEmpty()) {
			throw new Exception("QrCodeItem List Is Empty");
		}

		// URL logoURL =
		// PDFUtil.class.getClassLoader().getResource("images/co-optex-thumb-logo.png");

//		URL logoURL = QRCodeUtil.class.getClassLoader().getResource("images/co-optex-thumb-logo-lg.png");

		URL englishFontUrl = QRCodeUtil.class.getClassLoader().getResource("iText/PlayfairDisplay-Regular.ttf");

		String englishFont = englishFontUrl.getPath();

		Font generalFont = FontFactory.getFont(englishFont, fontSizeOne, Font.BOLD);

		Document document = new Document();
		// document.setMargins(40, 12, 2, 2);
		document.setMargins(10, 10, 2, 2);
		PdfWriter.getInstance(document, new FileOutputStream(qrCodeDataDTO.getPdfFilePath()));
		document.open();

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(0);

		// float[] columnWidths = { 7, 8 };

		for (QrCodeItemDTO qrCodeItemDTO : qrCodeItemDTOList) {

			try {
				PdfPTable itemWrapperTable = new PdfPTable(1);

				PdfPCell itemCell = new PdfPCell();
				PdfPTable qrImgTable = new PdfPTable(1);
				String qrCodeJsonTex = qrCodeItemDTO.getQrCodeJsonText();
				outputStream = QRCode.from(qrCodeJsonTex).to(ImageType.PNG).withSize(200, 200).stream();
				Image img = Image.getInstance(outputStream.toByteArray());
				PdfPCell imageCell = new PdfPCell(img, true);
				imageCell.setBorder(Rectangle.NO_BORDER);
				qrImgTable.addCell(imageCell);

				/*
				 * Add Image Table
				 */
				itemCell.addElement(qrImgTable);
				/*
				 * Add QR Code
				 */
				Font qrCodeFont = FontFactory.getFont(englishFont, fontSizeTwo, Font.BOLD);
				Paragraph qrCodeParagraph = new Paragraph(qrCodeItemDTO.getQrCode(), qrCodeFont);
				qrCodeParagraph.setAlignment(Element.ALIGN_CENTER);
				itemCell.addElement(qrCodeParagraph);
				// Add New Line
				itemCell.addElement(returnPhrase);
				/*
				 * Add Label One & Value
				 */
				PdfPTable table2 = new PdfPTable(2);
				// table2.setWidthPercentage(100);
				table2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell cell2 = null;
				// 1 - AT Number / Value
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getLabelOne(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getValueOne(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);
				// 2 - MRP / Value
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getLabelTwo(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getValueTwo(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);
				// 3 - Net Price / Value
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getLabelThree(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase(qrCodeItemDTO.getValueThree(), generalFont));
				cell2.setBorder(Rectangle.NO_BORDER);
				table2.addCell(cell2);

				//
				// itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				// itemCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				itemCell.addElement(table2);
				// Add New Line
				itemCell.addElement(returnPhrase);

				PdfPTable footerTable = new PdfPTable(2);
				footerTable.setWidthPercentage(100F);
				footerTable.setWidths(new int[] { 1, 3 });
				PdfPCell footerCell = null;
				footerCell = new PdfPCell();
				footerCell.setBorder(Rectangle.NO_BORDER);
//				Image image = Image.getInstance(logoURL);
//				image.setWidthPercentage(90F);
				// image.setScaleToFitHeight(true);
				// image.setAbsolutePosition(0f, 0f);
				footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//				footerCell.addElement(image);
				footerCell.setRowspan(2);
				footerTable.addCell(footerCell);

				// (GST Applicable)
				Font gstFont = FontFactory.getFont(englishFont, fontSizeOne, Font.BOLD);
				Paragraph labelFourParagraph = new Paragraph(qrCodeItemDTO.getLabelFour(), gstFont);
				labelFourParagraph.setAlignment(Element.ALIGN_CENTER);
				footerCell = new PdfPCell();
				footerCell.setBorder(Rectangle.NO_BORDER);
				footerCell.addElement(labelFourParagraph);
				footerTable.addCell(footerCell);
				//
				// itemCell.addElement(labelFourParagraph);
				// Add New Line
				itemCell.addElement(returnPhrase);
				Font productNameFont = FontFactory.getFont(englishFont, fontSizeOne, Font.BOLD);
				Paragraph labelFiveParagraph = new Paragraph(qrCodeItemDTO.getLabelFive(), productNameFont);
				labelFiveParagraph.setAlignment(Element.ALIGN_CENTER);
				footerCell = new PdfPCell();
				footerCell.setBorder(Rectangle.NO_BORDER);
				footerCell.addElement(labelFiveParagraph);
				footerTable.addCell(footerCell);
				//

				//
				// itemCell.addElement(labelFiveParagraph);

				itemCell.addElement(footerTable);

				itemWrapperTable.addCell(itemCell);

				//
				table.addCell(itemWrapperTable);
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}

		}

		document.add(table);

		document.close();
	}


}
