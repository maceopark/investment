
import cx_Oracle
import collections

class Connection(cx_Oracle.Connection):

    def cursor(self):
        return Cursor(self)

class Cursor(cx_Oracle.Cursor):

    def execute(self, statement, args = None):
        prepareNeeded = (self.statement != statement)
        result = super(Cursor, self).execute(statement, args or [])
        if prepareNeeded:
            description = self.description
            if description:
                names = [d[0] for d in description]
                self.rowfactory = collections.namedtuple("GenericQuery", names)

        return result
