function triggerEvent(element, event) {
    if (document.createEvent) {
        // IE�ȊO
        var evt = document.createEvent("HTMLEvents");
        evt.initEvent(event, true, true ); // event type, bubbling, cancelable
        return element.dispatchEvent(evt);
    } else {
        // IE
        var evt = document.createEventObject();
        return element.fireEvent("on"+event, evt)
    }
 }

  /**
   * Ajax �ɂ�� Action �փ��N�G�X�g�𔭍s���A�ΏۂƂȂ�Z���N�g�{�b�N�X�̃��X�g��ύX���܂��B<br />
   * ���̊֐��́A����Z���N�g�{�b�N�X�ō��ڂ��I�����ꂽ�ۂɁA�I�����ꂽ�l�ɉ����Ċ֘A����Z���N�g�{�b�N�X�̓��e�𓮓I��(�񓯊�)�ύX�������ꍇ�Ɏg�p���܂��B<br />
   * Action �N���X���ŁA�ȉ��̎������K�v�ɂȂ�܂��B
   * <ul>
   * <li>�I��l���A���N�G�X�g�p�����[�^�� "selected_value_" �Ŏ擾����B</li>
   * <li>�Z���N�g�{�b�N�X�̃f�[�^�́AJSON �`���ŕԂ��B</li>
   * <li>JSON �f�[�^�́AsqlSelect�^�O�Ɠ��l�ɁA���x����"LABEL"�A�R�[�h��"VALUE" �Ƃ����ꗗ�`���ō쐬����B</li>
   * </ul>
   *
   * @param {String} action ���N�G�X�g��� Action ��
   * @param {String} value �I��l
   * @param {String} targetId �ΏۂƂȂ�v���_�E���v�f��id�����l
   * @param {Boolean} empty ���X�g�̐擪���ڂɋ󔒍��ڂ�}�����邩�ǂ����̃t���O
   * @param {Boolean} selected �Ώۃv���_�E���œ����l������ꍇ�ɑI����Ԃɂ��邩�ǂ����̃t���O
   * @example // �C�x���g������ &lt;select id="item_select1"name="_.item_select1"
   *          onchange="St.ui.ajaxSelectList('/sample/v00101_01.action',
   *          this.value, 'item_select2')"&gt; // �ύX�Ώ� &lt;select
   *          id="item_select2" name="_.item_select2"&gt;
   */
  var ajaxSelectList = function (url, value, targetId, empty, selected) {
    //   var url = St.c.getActionUrl(action);
      var data = "selected_value=" + value;
      var success = function (response) {
          updateSelectList(targetId, response, empty, selected);
      }
      var error = function (xhr, status, err) {
          alert("[error] ajaxSelectList");
      }
      sendAjaxRequest(url, data, success, error);
  };


  /**
   * �w�肳�ꂽ�Z���N�g�{�b�N�X�̃��X�g��ύX���܂��B<br />
   * JSON �f�[�^�ɂ́AsqlSelect�^�O�Ɠ��l�ɁA���x����"LABEL"�A�R�[�h��"VALUE" �Ƃ����ꗗ�`���̃f�[�^���w�肷��B
   *
   * @param {String} targetId �ΏۂƂȂ�v���_�E���v�f��id�����l
   * @param {Object} data �ݒ肷��JSON�f�[�^
   * @param {Boolean} empty ���X�g�̐擪���ڂɋ󔒍��ڂ�}�����邩�ǂ����̃t���O
   * @param {Boolean} selected �Ώۃv���_�E���œ����l������ꍇ�ɑI����Ԃɂ��邩�ǂ����̃t���O
   */
  var updateSelectList = function (targetId, data, empty, selected) {
      var target = document.getElementById(targetId);
      var currentValue = target.value;
      var option, val, lab;
      target.innerHTML = "";
      if (empty) {
          var elem = document.createElement("option");
          elem.value = "";
          target.appendChild(elem);
      }
      for (var i in data) {
          option = document.createElement("option");
          val = data[i].value;
          lab = data[i].label;
          option.value = typeof val === "undefined" ? "" : val;
          option.textContent = typeof lab === "undefined" ? "" : lab;
          if (selected && val == currentValue) {
              option.selected = true;
          }
          target.appendChild(option);
      }
      triggerEvent(target, "change");
  };


  /**
   * �w�肳�ꂽ�f�[�^���A�񓯊��ő��M���܂��B
   *
   * @param {String} url ���M���URL
   * @param {String} data ���M����f�[�^
   * @param {Function} success �ʐM�������̃R�[���o�b�N�֐�
   * @param {Function} error �ʐM�G���[���̃R�[���o�b�N�֐�
   *
   */
  var sendAjaxRequest = function (url, data, success, error) {
      var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function () {
          if (xhr.readyState === 4) {
              var statusCode = xhr.status;
              if (200 <= statusCode && statusCode < 300) {
                  var response = JSON.parse(xhr.responseText);
                  success(response, statusCode);
              } else {
                  var statusText = xhr.statusText;
                  var err = xhr.statusText;
                  if (statusCode || !statusText) {
                      statusText = "error";
                  }
                  error(xhr, statusText, err);
              }
          }
      };
      xhr.open("GET", url + "?" + data, true);
    //   xhr.setRequestHeader("Content-Type",
    //       "application/x-www-form-urlencoded");
    //   xhr.dataType = "json";
      try {
          xhr.send(data);
      } catch (e) {
          xhr.abort();
      }
  }


  /**
   * �R���e�L�X�g�p�X�̒l��Ԃ��܂��B ���[�g�R���e�L�X�g�̏ꍇ�́A�󕶎�("")��Ԃ��܂��B
   *
   * @return �R���e�L�X�g�p�X
   * @example var contextPath = St.c.getContextPath(); // contextPath :
   *          "/sample" ([sample]�V�X�e���̏ꍇ)
   */
  var getContextPath = function () {
      var jsPath = "/stc/js/st.base.js";
      var selector = "head > script[src*='" + jsPath + "']";
      var obj = document.querySelector(selector);
      var src = obj.getAttribute("src");
      var index = src.indexOf(jsPath);
      if (index > 0) {
          return src.substring(0, index);
      } else {
          return "";
      }
  };

  /**
   * �w�肳�ꂽ Action ���Ăяo�� URL ��Ԃ��܂��B
   *
   * @param {String} action Action ��
   * @return {String} Action URL
   * @example var actionUrl = St.c.getActionUrl("v00101"); // actionUrl :
   *          "/�R���e�X�g�p�X/v00101.action"
   */
  var getActionUrl = function (action) {
      var path = getContextPath();
      if (action.charAt(0) != "/") {
          path = path + "/";
      }
      return path + action + ".action";
  };