package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.common.ProjectProvide;
import com.example.SpringGroupBB.dto.ProductDTO;
import com.example.SpringGroupBB.entity.Product;
import com.example.SpringGroupBB.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
  
  private final ProductRepository productRepository;
  private final ProjectProvide projectProvide;

  public List<Product> searchProductAll;

  public List<Product> searchProductAll() {
    return productRepository.findAll();
  }

  public ProductDTO searchProduct(Long id) {
    return ProductDTO.entityToDto(productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 센서를 찾을 수 없습니다: " + id)));
  }

  public void insertProduct(MultipartFile sFile, String realPath, ProductDTO productDTO) {
    String oFileName = sFile.getOriginalFilename();

    String sFileName = new SimpleDateFormat("yyyyMMddHHmmss")
            .format(new Date())+"_"+oFileName;

    try {
      projectProvide.writeFile(sFile, sFileName, realPath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    productDTO.setSensorImage(sFileName);
    Product product = Product.dtoToEntity(productDTO);
    productRepository.save(product);
  }

  public String deleteProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 센서입니다."));
    productRepository.deleteById(id);
    return "OK";
  }

  public boolean updateProduct(ProductDTO productDTO, String realPath) {
    Product oldProduct = productRepository.findById(productDTO.getId())
            .orElseThrow(()-> new IllegalArgumentException("해당 ID의 센서를 찾을 수 없습니다: " + productDTO.getId()));
    try {
      if(!oldProduct.getModel().equals(productDTO.getModel())) {
        Optional<Product> existingModel = productRepository.findByModel(productDTO.getModel());
        if (existingModel.isPresent()) {
          throw new IllegalArgumentException("이미 존재하는 모델명이므로 변경할 수 없습니다.");
        }
      }
      if(productDTO.getSFile()!=null && !productDTO.getSFile().isEmpty()) {
        String oFileName = productDTO.getSFile().getOriginalFilename();
        String sFileName = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date())+"_"+oFileName;

        projectProvide.writeFile(productDTO.getSFile(), sFileName, realPath);

        File oldFile = new File(realPath, oldProduct.getSensorImage());
        if (oldFile.exists()) oldFile.delete();

        oldProduct.setSensorImage(sFileName);
      }

      oldProduct.setSensorName(productDTO.getSensorName());
      oldProduct.setModel(productDTO.getModel());
      oldProduct.setSensorType(productDTO.getSensorType());
      oldProduct.setShortDescription(productDTO.getShortDescription());
      oldProduct.setFeatures(productDTO.getFeatures());
      oldProduct.setManufacturer(productDTO.getManufacturer());

      productRepository.save(oldProduct);

      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    }
  }
  public List<ProductDTO> selectSearchStr(String searchStr) {
    return ProductDTO.entityListToDTOList(productRepository.selectSearch(searchStr));
  }
}
