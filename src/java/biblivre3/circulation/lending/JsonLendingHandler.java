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
import biblivre3.circulation.CirculationDAO;
import biblivre3.circulation.UserDTO;
import biblivre3.enums.LendingRules;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.DTOCollection;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonLendingHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("list_lent")) {
            dto = listLent(request);
        } else if (submitButton.equals("lend")) {
            dto = lend(request);
        } else if (submitButton.equals("renew")) {
            dto = lendRenew(request);
        } else if (submitButton.equals("return")) {
            dto = lendReturn(request);
        } else if (submitButton.equals("pay_fine")) {
            dto = payFine(request);
        } else if (submitButton.equals("list_all_lendings")) {
            dto = listAll();
        }
        return dto.toJSONObject(properties);
    }
    
    public IFJson search(final HttpServletRequest request) {
        final String asset = request.getParameter("SEARCH_HOLDING");
        final String barcode = request.getParameter("SEARCH_SERIAL");

        if (StringUtils.isBlank(asset) && StringUtils.isBlank(barcode)) {
            return new ErrorDTO("MESSAGE_HOLDING_SEARCH_INVALID_OR_EMPTY", "warning");          
        }

        HoldingDTO hdto = null;
        if (StringUtils.isNotBlank(asset)) {
            hdto = (new HoldingBO()).getByAsset(asset);
        } else if (StringUtils.isNotBlank(barcode)) {
            hdto = (new HoldingBO()).getById(Integer.parseInt(barcode));
        }
        
        if (hdto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_SERIAL_NOT_FOUND", "warning");
        }

        LendingInfoDTO lidto = new LendingInfoDTO(hdto);

        return lidto;
    }

    public IFJson listLent(final HttpServletRequest request) {
        final String userId = request.getParameter("user_id");

        DTOCollection<LendingInfoDTO> licdto = new DTOCollection<LendingInfoDTO>();
        LendingBO lbo = new LendingBO();
        LendingFineBO fineBO = new LendingFineBO();

        try {
            int userSerial = Integer.valueOf(userId);
            UserDTO user = (new CirculationDAO()).searchByUserId(userSerial);
            ArrayList<LendingDTO> aldto = (ArrayList<LendingDTO>) lbo.listLendings(user);

            for (LendingDTO lendingDTO : aldto) {
                LendingInfoDTO dto = new LendingInfoDTO(lendingDTO);
                Integer daysLate = fineBO.getDaysLate(lendingDTO.getReturnDate());

                dto.setDaysLate(daysLate);
                dto.setFineValue(fineBO.calculateFineValue(daysLate));

                licdto.add(dto);
            }
        } catch (Exception e) {
        }

        return licdto;
    }

    public IFJson listAll() {
        DTOCollection<LendingInfoDTO> result = new DTOCollection<LendingInfoDTO>();
        LendingBO lbo = new LendingBO();
        LendingFineBO fineBO = new LendingFineBO();
        List<LendingDTO> searchResults = lbo.listAll();
        for (LendingDTO lendingDTO : searchResults) {
            LendingInfoDTO dto = new LendingInfoDTO(lendingDTO);
            Integer daysLate = fineBO.getDaysLate(lendingDTO.getReturnDate());
            dto.setDaysLate(daysLate);
            dto.setFineValue(fineBO.calculateFineValue(daysLate));
            result.add(dto);
        }
        return result;
    }


    public IFJson lend(final HttpServletRequest request) {
        final String holding = request.getParameter("holding_serial");
        final String userId = request.getParameter("user_id");

        HoldingDTO hdto = null;
        try {
            hdto = (new HoldingBO()).getById(Integer.valueOf(holding));
        } catch (Exception e) {
        }

        if (hdto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_SERIAL_NOT_FOUND", "warning");
        }

        UserDTO udto = null;
        try {
            udto = (new CirculationBO()).searchByUserId(Integer.valueOf(userId));
        } catch (Exception e) {
        }

        if (udto == null) {
            return new ErrorDTO("MESSAGE_USER_SERIAL_NOT_FOUND", "warning");
        }


        final LendingBO bo = new LendingBO();
        final LendingRules result = bo.checkLending(hdto, udto);

        if (!result.equals(LendingRules.LENDING_POSSIBLE)) {
            return new ErrorDTO("MESSAGE_" + result.name(), "warning");
        } else if (bo.doLend(hdto, udto)) {
            LendingInfoDTO lidto = new LendingInfoDTO(hdto);
            lidto.setMessage("SUCCESS_LEND");
            return lidto;
        } else {
            return new ErrorDTO("ERROR_LEND", "warning");
        }
    }

    public IFJson lendRenew(final HttpServletRequest request) {
        final String holding = request.getParameter("holding_serial");
        final String userId = request.getParameter("user_id");

        HoldingDTO hdto = null;
        try {
            hdto = (new HoldingBO()).getById(Integer.valueOf(holding));
        } catch (Exception e) {
        }

        if (hdto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_SERIAL_NOT_FOUND", "warning");
        }

        UserDTO udto = null;
        try {
            udto = (new CirculationBO()).searchByUserId(Integer.valueOf(userId));
        } catch (Exception e) {
        }

        if (udto == null) {
            return new ErrorDTO("MESSAGE_USER_SERIAL_NOT_FOUND", "warning");
        }

        LendingBO bo = new LendingBO();
        LendingDTO ldto = bo.getByHolding(hdto);
        LendingFineBO fineBo = new LendingFineBO();

        if (ldto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_NOT_LENT", "warning");
        } else if (fineBo.isLateReturn(ldto)) {
            return new ErrorDTO("MESSAGE_LATE_RETURN", "warning");
        }

        final LendingRules result = bo.checkRenew(hdto, udto);
        if (!result.equals(LendingRules.LENDING_POSSIBLE)) {
            return new ErrorDTO("MESSAGE_" + result.name(), "warning");
        } else if (bo.doRenew(ldto)) {
            LendingInfoDTO lidto = new LendingInfoDTO(hdto);
            lidto.setMessage("SUCCESS_RENEW");
            return lidto;
        } else {
            return new ErrorDTO("ERROR_RENEW", "warning");
        }
    }

    public IFJson lendReturn(final HttpServletRequest request) {
        final String holding = request.getParameter("holding_serial");
        final String userId = request.getParameter("user_id");
        final String fineValue = request.getParameter("fine_value");
        final String fineAction = request.getParameter("fine_action");

        int iUserId = 0;
        try {
            iUserId = Integer.valueOf(userId);
        } catch (Exception e) {}

        HoldingDTO hdto = null;

        try {
            HoldingBO hbo = new HoldingBO();
            hdto = hbo.getById(Integer.valueOf(holding));
        } catch (Exception e) {}

        if (hdto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_SERIAL_NOT_FOUND", "warning");
        }

        UserDTO udto = null;

        try {
            CirculationBO cbo = new CirculationBO();
            udto = cbo.searchByUserId(iUserId);
        } catch (Exception e) {
        }

        if (udto == null) {
            return new ErrorDTO("MESSAGE_USER_SERIAL_NOT_FOUND", "warning");
        }

        LendingBO bo = new LendingBO();
        LendingDTO ldto = bo.getByHolding(hdto);

        if (ldto == null) {
            return new ErrorDTO("MESSAGE_HOLDING_NOT_LENT", "warning");
        }

        if (bo.doReturn(ldto, udto, fineValue, fineAction.equals("pay"))) {
            LendingInfoDTO lidto = new LendingInfoDTO(hdto, iUserId);
            lidto.setMessage("SUCCESS_RETURN");
            return lidto;
        } else {
            return new ErrorDTO("ERROR_RETURN", "warning");
        }
    }

    private IFJson payFine(HttpServletRequest request) {
        final String fineId = request.getParameter("fine_id");
        final String action = request.getParameter("action");
        LendingFineBO lfbo = new LendingFineBO();
        LendingFineDTO dto = lfbo.getById(Integer.valueOf(fineId));
        Date today = new Date();
        dto.setPayment(today);
        if (action.equals("accredit")) {
            dto.setValue(0f);
        }
        if (lfbo.update(dto)) {
            return new SuccessDTO("SUCCESS_PAY_FINE");
        } else {
            return new ErrorDTO("ERROR_PAY_FINE", "error");
        }
    }
}
