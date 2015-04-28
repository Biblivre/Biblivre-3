<fieldset class="noborder">
    <div>
        <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR"/></div>

        <div class="fcenter">
            <select name="author_type" onchange="Cataloging.toggleAreas(this.name, this.value);">
                <option value=""><i18n:getText module="biblivre3" textKey="VALUE_SELECT_TYPE_AUTHOR"/></option>
                <option value="100"><i18n:getText module="biblivre3" textKey="b_100"/></option>
                <option value="111"><i18n:getText module="biblivre3" textKey="b_111"/></option>
                <option value="110"><i18n:getText module="biblivre3" textKey="b_110"/></option>
                <option value="130"><i18n:getText module="biblivre3" textKey="b_130"/></option>
            </select>
        </div>

        <div class="clear"></div>
    </div>
</fieldset>

<div class="author_type" data="100" style="display: none;">
    <fieldset class="datafield" data="100">
        <legend><i18n:getText module="biblivre3" textKey="b_100"/></legend>

        <div class="indicator">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_i1"/></div>

            <div class="fcenter">
                <select name="ind1">
                    <option value="1"><i18n:getText module="biblivre3" textKey="b_100_i1_1"/></option>
                    <option value="0"><i18n:getText module="biblivre3" textKey="b_100_i1_0"/></option>
                    <option value="2"><i18n:getText module="biblivre3" textKey="b_100_i1_2"/></option>
                    <option value="3"><i18n:getText module="biblivre3" textKey="b_100_i1_3"/></option>
                </select>
            </div>

            <div class="clear"></div>
        </div>

        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_a"/></div>
            <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" id="b_100_a" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_b"/></div>
            <div class="fcenter"><input type="text" name="b" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_c"/></div>
            <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_d"/></div>
            <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_100_q"/></div>
            <div class="fcenter"><input type="text" name="q" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
    </fieldset>
</div>

<div class="author_type" data="110" style="display: none;">
    <fieldset class="datafield" data="110">
        <legend><i18n:getText module="biblivre3" textKey="b_110"/></legend>

        <div class="indicator">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_i1"/></div>

            <div class="fcenter">
                <select name="ind1">
                    <option value="0"><i18n:getText module="biblivre3" textKey="b_110_i1_0"/></option>
                    <option value="1"><i18n:getText module="biblivre3" textKey="b_110_i1_1"/></option>
                    <option value="2"><i18n:getText module="biblivre3" textKey="b_110_i1_2"/></option>
                </select>
            </div>

            <div class="clear"></div>
        </div>

        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_a"/></div>
            <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" id="b_110_a" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_b"/></div>
            <div class="fcenter"><input type="text" name="b" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_c"/></div>
            <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_d"/></div>
            <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_l"/></div>
            <div class="fcenter"><input type="text" name="l" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_110_n"/></div>
            <div class="fcenter"><input type="text" name="n" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
    </fieldset>
</div>

<div class="author_type" data="111" style="display: none;">
    <fieldset class="datafield" data="111">
        <legend><i18n:getText module="biblivre3" textKey="b_111"/></legend>

        <div class="indicator">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_i1"/></div>

            <div class="fcenter">
                <select name="ind1">
                    <option value="0"><i18n:getText module="biblivre3" textKey="b_111_i1_0"/></option>
                    <option value="1"><i18n:getText module="biblivre3" textKey="b_111_i1_1"/></option>
                    <option value="2"><i18n:getText module="biblivre3" textKey="b_111_i1_2"/></option>
                </select>
            </div>

            <div class="clear"></div>
        </div>

        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_a"/></div>
            <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" id="b_111_a" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_c"/></div>
            <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_d"/></div>
            <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_e"/></div>
            <div class="fcenter"><input type="text" name="e" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_g"/></div>
            <div class="fcenter"><input type="text" name="g" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_k"/></div>
            <div class="fcenter"><input type="text" name="k" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_111_n"/></div>
            <div class="fcenter"><input type="text" name="n" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
    </fieldset>
</div>

<div class="author_type" data="130" style="display: none;">
    <fieldset class="datafield" data="130">
        <legend><i18n:getText module="biblivre3" textKey="b_130"/></legend>

        <div class="indicator">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_i1"/></div>

            <div class="fcenter">
                <select name="ind1">
                    <option value="0"><i18n:getText module="biblivre3" textKey="b_130_i1_0"/></option>
                    <option value="1"><i18n:getText module="biblivre3" textKey="b_130_i1_1"/></option>
                    <option value="2"><i18n:getText module="biblivre3" textKey="b_130_i1_2"/></option>
                    <option value="3"><i18n:getText module="biblivre3" textKey="b_130_i1_3"/></option>
                    <option value="4"><i18n:getText module="biblivre3" textKey="b_130_i1_4"/></option>
                    <option value="5"><i18n:getText module="biblivre3" textKey="b_130_i1_5"/></option>
                    <option value="6"><i18n:getText module="biblivre3" textKey="b_130_i1_6"/></option>
                    <option value="7"><i18n:getText module="biblivre3" textKey="b_130_i1_7"/></option>
                    <option value="8"><i18n:getText module="biblivre3" textKey="b_130_i1_8"/></option>
                    <option value="9"><i18n:getText module="biblivre3" textKey="b_130_i1_9"/></option>
                </select>
            </div>

            <div class="clear"></div>
        </div>

        <div class="indicator">
            <input type="hidden" class="dont_clear" name="ind2" value="4" />
            <div class="clear"></div>
        </div>


        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_a"/></div>
            <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_d"/></div>
            <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_f"/></div>
            <div class="fcenter"><input type="text" name="f" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_g"/></div>
            <div class="fcenter"><input type="text" name="g" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_k"/></div>
            <div class="fcenter"><input type="text" name="k" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_l"/></div>
            <div class="fcenter"><input type="text" name="l" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
        <div class="subfield repeatable">
            <div class="fleft"><i18n:getText module="biblivre3" textKey="b_130_p"/></div>
            <div class="fcenter"><input type="text" name="p" maxlength="256" class="finput" /></div>
            <div class="clear"></div>
        </div>
    </fieldset>
</div>
