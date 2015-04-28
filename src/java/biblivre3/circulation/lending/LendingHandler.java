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

package biblivre3.circulation.lending;

import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.DTOCollection;

public class LendingHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {

        if (submitButton.equals("LENDING_RECEIPT")) {
            String strItems = request.getParameter("items");
            String strUserSerial = request.getParameter("user_serial");

            String[] items = strItems.split(",");

            DTOCollection<LendingInfoDTO> lendingCollectionDTO = null;
            DTOCollection<LendingInfoDTO> renewCollectionDTO = null;
            DTOCollection<LendingInfoDTO> returnCollectionDTO = null;

            HoldingBO hbo = new HoldingBO();
            HoldingDTO hdto;

            for (String item : items) {
                String[] arrItem = item.split("_");
                String action = arrItem[0];
                String userSerial = arrItem[1];
                Integer holdingSerial = Integer.parseInt(arrItem[2]);

                if (!userSerial.equals(strUserSerial)) {
                    continue;
                }

                int iUserSerial = 0;
                try {
                    iUserSerial = Integer.parseInt(userSerial);
                } catch (Exception e) {}

                if (action.equals("lending")) {
                    if (lendingCollectionDTO == null) {
                        lendingCollectionDTO = new DTOCollection<LendingInfoDTO>();
                    }

                    hdto = hbo.getById(holdingSerial);
                    lendingCollectionDTO.add(new LendingInfoDTO(hdto, iUserSerial));
                } else if (action.equals("renew")) {
                    if (renewCollectionDTO == null) {
                        renewCollectionDTO = new DTOCollection<LendingInfoDTO>();
                    }

                    hdto = hbo.getById(holdingSerial);
                    renewCollectionDTO.add(new LendingInfoDTO(hdto, iUserSerial));
                } else if (action.equals("return")) {
                    if (returnCollectionDTO == null) {
                        returnCollectionDTO = new DTOCollection<LendingInfoDTO>();
                    }
                    hdto = hbo.getById(holdingSerial);
                    try {
                        LendingInfoDTO lend = new LendingInfoDTO(hdto, iUserSerial);
                        returnCollectionDTO.add(lend);
                    } catch (Exception e) {

                    }
                }
            }

            CirculationBO cbo = new CirculationBO();
            UserDTO udto = cbo.searchByUserId(Integer.parseInt(strUserSerial));

            request.setAttribute("LENDING_READER_NAME", udto.getName());
            request.setAttribute("LENDING_READER_ENROL", udto.getUserid());
            request.setAttribute("LENDING_BIBLIO_NAME", Config.getConfigProperty(ConfigurationEnum.LIBRARY_NAME.name()));
            request.setAttribute("LENDING_RECEIPT_LENDING_LIST", lendingCollectionDTO);
            request.setAttribute("LENDING_RECEIPT_RENEW_LIST", renewCollectionDTO);
            request.setAttribute("LENDING_RECEIPT_RETURN_LIST", returnCollectionDTO);

            return "/jsp/circulation/lending_receipt.jsp";

        } else {
            return "";
        }
    }
}