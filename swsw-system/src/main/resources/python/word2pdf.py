import sys

from win32com.client import Dispatch

wdFormatPDF = 17


def doc2pdf(input_files):
    global word
    try:
        word = Dispatch('KWPS.application')
    except:
        word = Dispatch('WORD.application')
    finally:
        doc = word.Documents.Open(input_files)
        if input_files.endswith(".docx"):
            doc.SaveAs(input_files.replace(".docx", ".pdf"), FileFormat=wdFormatPDF)
        elif input_files.endswith(".doc"):
            doc.SaveAs(input_files.replace(".doc", ".pdf"), FileFormat=wdFormatPDF)
        doc.Close()
        word.Quit()


if __name__ == '__main__':
    input_file = sys.argv[1]
    doc2pdf(input_file)
