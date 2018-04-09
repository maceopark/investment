
import os

# default values
MAIN_USER = "datacrawl"
MAIN_PASSWORD = "tt"
CONNECT_STRING = "localhost/investment"

# calculated values based on the values above
CONNECT_STRING = "%s/%s@%s" % (MAIN_USER, MAIN_PASSWORD, CONNECT_STRING)

LR_K = 0.5


os.environ["NLS_LANG"] = "American_America.UTF8"

