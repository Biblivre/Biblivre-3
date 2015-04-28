/**
 *  Este arquivo é parte do Biblivre3.
 *  
 *  Biblivre3 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */

package mercury;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.fileupload.FileItem;

public class DigitalMediaDAO extends DAO {

    public final Integer uploadToDigitalMedia(FileItem file) {
        Connection con = null;
        Integer serial = 0;
        String filename = file.getName();

        if (filename.lastIndexOf('\\') != -1) {
            filename = filename.substring(filename.lastIndexOf('\\') + 1);
        }

        if (filename.lastIndexOf('/') != -1) {
            filename = filename.substring(filename.lastIndexOf('/') + 1);
        }

        try {
            InputStream is = file.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            serial = getNextSerial("digital_media_id_seq");
            if (serial != 0) {
                con = getDataSource().getConnection();
                con.setAutoCommit(false);
                String sql =
                        " INSERT INTO digital_media " +
                        " (id, file, mime_type, file_name) " +
                        " VALUES (?, ?, ?, ?) ";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setInt(1, serial);
                pstm.setBinaryStream(2, bis, (int) file.getSize());
                pstm.setString(3, file.getContentType());
                pstm.setString(4, filename);
                pstm.executeUpdate();
                pstm.close();
                is.close();
                con.commit();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return serial;
    }

    public final DigitalMediaDTO getDigitalMedia(int id, String fileName) {
        Connection con = null;
        byte[] blob = null;
        DigitalMediaDTO dto = new DigitalMediaDTO();
        try {
            con = getDataSource().getConnection();
            String sql = 
                    " SELECT file, file_name, mime_type FROM digital_media " +
                    " WHERE id = ? " +
                    " AND file_name = ? ;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, fileName);
            con.setAutoCommit(false);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                blob = rs.getBytes(1);
                if (blob == null) {
                    return null;
                }
                dto.setIn(new ByteArrayInputStream(blob));
                dto.setLength((int) blob.length);
                dto.setFileName(rs.getString(2));
                dto.setMimeType(rs.getString(3));
            }
            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }


}
