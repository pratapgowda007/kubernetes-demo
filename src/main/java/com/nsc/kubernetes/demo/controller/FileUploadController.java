package com.nsc.kubernetes.demo.controller;

import com.nsc.kubernetes.demo.exception.InvalidFileException;
import com.nsc.kubernetes.demo.model.PatientBannerConfiguration;
import com.nsc.kubernetes.demo.repository.ItemRepository;
import com.nsc.kubernetes.demo.repository.PatientBannerConfigurationRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/patient-banner/configuration")
public class FileUploadController {

    @Autowired
    private PatientBannerConfigurationRepository patientBannerConfigurationRepository;

    @Autowired
    private ItemRepository itemRepository;

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Iterable<PatientBannerConfiguration> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        List<PatientBannerConfiguration> patientBannerConfigurationList = new ArrayList<>();

        Workbook workbook;

        if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith("xlsx")) {
            workbook = new XSSFWorkbook(multipartFile.getInputStream());
        } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
            workbook = new HSSFWorkbook(multipartFile.getInputStream());
        } else {
            throw new InvalidFileException("The specified file is not Excel file");
        }

        List<PatientBannerConfiguration> itemList = new ArrayList<>();

        Sheet patientBannerSheet = workbook.getSheet("Patient_Banner");

        Iterator<Row> iterator = patientBannerSheet.iterator();
        iterator.next(); // ignoring header row

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            PatientBannerConfiguration patientBannerConfiguration = new PatientBannerConfiguration();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int columnIndex = currentCell.getColumnIndex();

                switch (columnIndex) {
                    case 0:
                        String patientBannerID = currentCell.getStringCellValue();
                        patientBannerConfiguration.setPatientBannerId(patientBannerID);
                        break;
                    case 1:
                        String isDefault = currentCell.getStringCellValue();
                        patientBannerConfiguration.setDefault("Y".equals(isDefault));
                        break;
                    case 2:
                        String unitId = currentCell.getStringCellValue();
                        patientBannerConfiguration.setUnitId(unitId);
                        break;
                    case 3:
                        String clinicalItemIds = currentCell.getStringCellValue();
                        clinicalItemIds = clinicalItemIds.replaceAll("(?m)^[ \t]*\r?\n", "");
                        String[] ids = clinicalItemIds.split("\\r?\\n");
                        patientBannerConfiguration.setClinicalItemList(Arrays.asList(ids));
                        break;
                    default:
                        break;
                }
            }
            if (patientBannerConfiguration.getPatientBannerId() != null) {
                patientBannerConfiguration.setOrganizationId("Org_01");
                patientBannerConfigurationList.add(patientBannerConfiguration);
            }

        }
        return patientBannerConfigurationRepository.saveAll(patientBannerConfigurationList);
    }

    @RequestMapping(path = "/create-file", method = RequestMethod.GET)
    public String createFile() throws IOException {
        String fileContent = "Hello Pratap, how are you?";
        FileWriter fileWriter = new FileWriter("/temp/pratap/test/samplefile.txt");
        fileWriter.write(fileContent);
        fileWriter.close();

        return "file write successful";
    }

    @RequestMapping(path = "/read-file", method = RequestMethod.GET)
    public String readFile() throws IOException {
        FileReader fileReader = new FileReader("/temp/pratap/test/samplefile.txt");
        StringBuilder content = new StringBuilder();
        int i;
        while ((i = fileReader.read()) != -1) {
            content.append((char) i);
        }
        fileReader.close();
        return content.toString();
    }

    @RequestMapping(path = "/read-env/{variable}", method = RequestMethod.GET)
    public String readEnvironmentVariable(@PathVariable String variable) {
        if (variable != null) {
            String value = System.getenv(variable);
            return "{\"" + variable + "\" : \"" + value + "\"}";
        }
        return "Not Found";
    }

    @RequestMapping(path = "/read-config/{name}", method = RequestMethod.GET)
    public String readConfig(@PathVariable String name) throws IOException {
        FileReader fileReader = new FileReader("/temp/pratap/config/" + name);
        StringBuilder content = new StringBuilder();
        int i;
        while ((i = fileReader.read()) != -1) {
            content.append((char) i);
        }
        fileReader.close();
        return content.toString();
    }

    @RequestMapping(path = "/banner/{bannerId}", method = RequestMethod.GET)
    public PatientBannerConfiguration getById(@PathVariable String bannerId) {
        Optional<PatientBannerConfiguration> patientBannerConfigurationOptional =
                patientBannerConfigurationRepository.findById(bannerId);
        return patientBannerConfigurationOptional.orElse(null);
    }

    @RequestMapping(path = "/org/{orgId}", method = RequestMethod.GET)
    public Iterable<PatientBannerConfiguration> getByOrgId(@PathVariable String orgId) {
        Optional<Iterable<PatientBannerConfiguration>> patientBannerConfigurationOptional =
                patientBannerConfigurationRepository.findByOrganizationId(orgId);
        return patientBannerConfigurationOptional.orElse(null);
    }

    @RequestMapping(path = "/unit/{unitId}", method = RequestMethod.GET)
    public PatientBannerConfiguration getByUnitId(@PathVariable String unitId) {
        Optional<PatientBannerConfiguration> patientBannerConfigurationOptional =
                patientBannerConfigurationRepository.findTopByUnitIdOrIsDefault(unitId, true);
        return patientBannerConfigurationOptional.orElse(null);
    }

    @RequestMapping(path = "/banner/{bannerId}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String bannerId) {
        patientBannerConfigurationRepository.deleteById(bannerId);
    }

    // No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call
    @Transactional
    @RequestMapping(path = "/org/{orgId}", method = RequestMethod.DELETE)
    public void deleteByOrgId(@PathVariable String orgId) {
        patientBannerConfigurationRepository.deleteByOrganizationId(orgId);
    }

    @Transactional
    @RequestMapping(path = "/item/cleanup", method = RequestMethod.GET)
    public void cleanUpBanner() {
        Set<String> ids = itemRepository.findAllClinicalItemIds();
        patientBannerConfigurationRepository.deleteItemsNotExistInItemTable(ids);
    }
}
