package com.huawei.spring.core.implement.SQLScan.util;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.huawei.spring.catalogue.SQLScan.SqlPackage;
import com.huawei.spring.catalogue.SQLScan.SqlScript;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotFoundException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

  @SuppressWarnings("resource")
  public static void unzip(String sourcePath, String targetPath) throws NotFoundException {
    File sourceFile = new File(sourcePath);
    if (!sourceFile.exists()) {
      throw new NotFoundException("File path does not exist!");
    }
    InputStream is = null;
    FileOutputStream os = null;
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(sourceFile, "GBK");
      for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements() ; ) {
        ZipEntry entity = (ZipEntry)entries.nextElement();
        File file = new File(targetPath + File.separator + entity.getName());
        if (entity.isDirectory()) {
          if (!file.mkdirs()) {
            throw new FileOperationException("File directory creation failed!");
          }
        } else {
          File parentFile = file.getParentFile();
          if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
              throw new FileOperationException("File directory creation failed!");
            }
          }
          try {
            is = zipFile.getInputStream(entity);
            os = new FileOutputStream(file);
            byte[] b = new byte[2048];
            int n = 0;
            while ((n = is.read(b)) != -1) {
              os.write(b, 0, n);
            }
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            if (null != is) {
              try {
                is.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            if (null != os) {
              try {
                os.flush();
              } catch (IOException e) {
                e.printStackTrace();
              }
              try {
                os.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != zipFile) {
        try {
          zipFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @SuppressWarnings("resource")
  public static void unrar(String sourcePath, String targetPath) throws NotFoundException {
//		File targetFolder = new File(targetPath);
//		if (!targetFolder.exists()) {
//			targetFolder.mkdirs();
//		}
    File sourceFile = new File(sourcePath);
    if (!sourceFile.exists()) {
      throw new NotFoundException("File path does not exist!");
    }
    Archive archive = null;
    try {
      archive = new Archive(sourceFile);
      FileHeader fileHeader = archive.nextFileHeader();
      while (null != fileHeader) {
        if (fileHeader.isDirectory()) {
          File targetDir = null;
          if (checkNameIsCn(fileHeader.getFileNameW())) {
            targetDir = new File(targetPath + File.separator + fileHeader.getFileNameW());
          } else {
            targetDir = new File(targetPath + File.separator + fileHeader.getFileNameString());
          }
//          if (!targetDir.mkdirs()) {
//            throw new FileOperationException("File directory creation failed!");
//          }
        } else {
          File targetFile = null;
          if (checkNameIsCn(fileHeader.getFileNameW())) {
            targetFile = new File(targetPath + File.separator + fileHeader.getFileNameW().trim());
          } else {
            targetFile = new File(targetPath + File.separator + fileHeader.getFileNameString().trim());
          }
          FileOutputStream os = null;
          try {
            if (!targetFile.exists()) {
              if (!targetFile.getParentFile().exists()) {
                if (!targetFile.getParentFile().mkdirs()) {
                  throw new FileOperationException("File directory creation failed!");
                }
              }
              if (!targetFile.createNewFile()) {
                throw new FileOperationException("File creation failed!");
              }
            }
            os = new FileOutputStream(targetFile);
            archive.extractFile(fileHeader, os);
          } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
          } finally {
            if (null != os) {
              try {
                os.flush();
              } catch (IOException e) {
                e.printStackTrace();
              }
              try {
                os.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
        fileHeader = archive.nextFileHeader();
      }
    } catch (RarException | IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != archive) {
          archive.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("resource")
  public static void untar(String sourcePath, String targetPath) {
    //TODO check sourceFile is exist!
    File sourceFile = new File(sourcePath);
    FileInputStream is;
    TarArchiveInputStream tais = null;
    FileOutputStream os = null;
    BufferedOutputStream bos = null;
    try {
      is = new FileInputStream(sourceFile);
      tais = new TarArchiveInputStream(is);
      TarArchiveEntry entry = null;
      while ((entry = tais.getNextTarEntry()) != null) {
        File targetFile = new File(targetPath + File.separator + entry.getName());
        if (entry.isDirectory()) {
          if (!targetFile.mkdirs()) {
            throw new FileOperationException("File directory creation failed!");
          }
        } else {
          try {
            os = new FileOutputStream(targetFile);
            bos = new BufferedOutputStream(os);
            byte[] b = new byte[4096];
            int n = 0;
            while ((n = tais.read(b)) != -1) {
              bos.write(b, 0, n);
            }
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              if (null != os) os.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
            try {
              if (null != bos) bos.flush();
            } catch (IOException e) {
              e.printStackTrace();
            }
            try {
              if (null != bos) bos.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != tais) tais.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static List<File> getAllSqlScriptFiles(String targetPath, List<File> list) throws NotFoundException {
    // List<File> list = new ArrayList<File>();
    File fileFolder = new File(targetPath);
    if (!fileFolder.exists()) {
      throw new NotFoundException("File path does not exist!");
    } else {
      File[] files = fileFolder.listFiles();
      if (null != files) {
        for (File file : files) {
          String fileName = file.getName();
          if (file.isDirectory()) {
            getAllSqlScriptFiles(file.getAbsolutePath(), list);
          } else if (fileName.endsWith(".sql")) {
            //System.out.println("----" + file.getAbsolutePath());
            list.add(file);
          }
        }
      }
    }
    return list;
  }

  public static List<SqlScript> getAllSqlScripts(List<File> files, SqlPackage sqlPackage) {
    List<SqlScript> sqlScripts = new ArrayList<>();
    for (File file : files) {
      long sizeKB = (long) Math.ceil((double) file.length() / 1024);
      SqlScript sqlScript = new SqlScript();
      sqlScript.setId(IdGenerator.createUUID());
      sqlScript.setName(file.getName());
      sqlScript.setVersion(sqlPackage.getVersion());
      sqlScript.setSize(sizeKB);
      sqlScript.setFilePath(file.getAbsolutePath());
      sqlScript.setUploaded(sqlPackage.getCreated());
      sqlScript.setBaseType(sqlPackage.getBaseType());
      sqlScript.setPackageId(sqlPackage.getId());
      sqlScript.setTaskId(sqlPackage.getTaskId());
      sqlScripts.add(sqlScript);
    }
    return sqlScripts;
  }

  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      //递归删除目录中的子目录下
      if (null != children) {
        for (String aChildren : children) {
          boolean success = deleteDir(new File(dir, aChildren));
          if (!success) return false;
        }
      }
    }
    // 目录此时为空，可以删除
    return dir.delete();
  }

  public static int getFileNumber(String path) {
    int count = 0;
    File directory = new File(path);
    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      for (File f : files) {
        if (f.isFile()) {
          count += 1;
        }
      }
    }
    return count;
  }

  private static boolean checkNameIsCn(String fileName) {
    String regEx = "[\\u4e00-\\u9fa5]";
    Pattern compile = Pattern.compile(regEx);
    Matcher matcher = compile.matcher(fileName);
    return matcher.find();
  }
}
